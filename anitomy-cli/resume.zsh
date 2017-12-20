#!/usr/bin/env zsh
setopt nocaseglob extendedglob
if (( $# != 1 )); then
    printf 'USAGE: %s dir\n' ${0:t}
    exit 2
fi
cd -- "$1" || exit $?

anitomy_cli=~/repo/anitomy-cli/anitomy-cli
exec ${=MPV:-mpv} \
    --script=~/.config/mpv/re.lua \
    --script-opts=re-anitomy-cli-cmd=$anitomy_cli \
    --save-position-on-quit \
    --keep-open \
    --pause=no \
    -- ${(0)"$(printf '%s\0' *.((mkv|mp4)) | $anitomy_cli -z sort)"}
