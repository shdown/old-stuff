This script is basically a lighter version of [deterenkelt/watch.sh](https://github.com/deterenkelt/watch.sh). What it does is:

  - build a media files list (flags involved are `-r` (recursive) and `-n` (numeric sort));

  - find and pass `--sub-paths` if the `-s` flag was specified;

  - pass `--sub-file`/`--audio-file` as per-file options if `-S`/`-a`/`-A` (subtitles numeric match/audio exact match/audio numeric match) flags were specified;

  - `exec mpv --save-position-on-quit …`
