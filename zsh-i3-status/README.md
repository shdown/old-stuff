zsh-i3-status is a statusbar generator program for [i3bar](https://github.com/i3/i3).

It’s written is zsh and **does absolutely no forks at the run time**.

Moreover, it’s `select`-driven, not timer-driven, which means widgets can be updated immediately as some events occur — with some help of external watchers.

It also supports click events.

How to use it
===
  - `mv config.lib.zsh{.example,}`;
  - edit `config.lib.zsh`;
  - specify `exec /path/to/status.zsh` as a `status_command` in a `bar` section of your i3 config.

Widget concept
===
Each *widget* is a function named `i3bar_widget_<widget name>`.

Once a widget wants to update, it prints a line with JSON dictionary to stdout.

An empty line means widget doesn’t want to be displayed at all.

Currently implemented widgets
===
  - time and date, with `strftime -s` from `zsh/datetime` (standard zsh module);
  - battery state, through parsing of `/sys/class/power_supply/…`;
  - network state, through parsing of `/proc/net/dev`;
  
  (these three above are merged into one widget in my config, `everything`.)
  - keyboard layout, with [xkb-switch](https://github.com/ierton/xkb-switch) (**updates immediately as keyboard layout changes**);
  - active window title, with [xtitle](https://github.com/baskerville/xtitle) (**updates immediately as window focus changes**);
  - ALSA channel volume and mute state, with [alsawatch](https://github.com/shdown/old-stuff/tree/master/alsawatch) (**updates immediately as channel state changes**).

Of course you can write your own widgets!

Why not to write it in bash
===
Because it lacks `select` and `strftime`.
