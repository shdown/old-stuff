#!/usr/bin/env zsh

# $0 is bad, but that's not a case.
# See http://mywiki.wooledge.org/BashFAQ/028#Why_.240_is_NOT_an_option
# (“:h” is “dirname”, “:a” turns a file name into an absolute path)
WORKDIR=${0:a:h}
# If WORKDIR detection failed, exit now.
source $WORKDIR/config.lib.zsh || exit $?

if [[ -n $I3BAR_CLICKEVENTS_LISTENER ]]; then
    print '{"version": 1, "click_events": true}'
    # Redirect its stdout to stderr to not interfere with our output.
    $I3BAR_CLICKEVENTS_LISTENER >&2 &
else
    print '{"version": 1}'
fi
print '['

# Kill all children on SIGINT/SIGTERM.
trap '
    # Reset all “traps” (signal handlers)
    trap -
    # Kill the current process group
    kill 0' SIGINT SIGTERM

_multiplexer() {
    zmodload zsh/zselect
    unset      outputs fds
    typeset -a outputs fds

    for fd_path; do
        # /dev/fd/<->
        # /proc/self/fd/<->
        if [[ $fd_path == */fd/<-> ]]; then
            # “:t” is “basename”
            fd=${fd_path:t}
        else
            print -r >&2 "E: can’t extract fd from “$fd_path”."
            return 1
        fi
        fds+=$fd
    done

    while true; do
        zselect -a ready_fds -r $fds
        # “:1” to drop initial “-r” from ready_fds.
        for fd in ${ready_fds:1}; do
            read -r -t -u $fd "outputs[$fd]"
        done
        # Zsh drops empty array elements if not double-quoted. Yay!
        print -r '['${(j:,:)${outputs}}'],'
    done
}

() { # anonymous function
    local widget line='_multiplexer'
    for widget in $I3BAR_WIDGETS; do
        line+=" <(i3bar_widget_${(q)widget})"
    done
    exec eval "$line"
}
