utils = require "mp.utils"
msg = require "mp.msg"

--------------------------------------------------------------------------------

function get_dir()
    local path = mp.get_property("path", "")
    local dir = ""
    if not path:match("://") then
        dir = utils.split_path(path)
    end
    if dir ~= "" and dir ~= "." then -- [mpv on] Windows is terrible.
        return utils.join_path(utils.getcwd(), dir)
    else
        return utils.getcwd()
    end
end

--------------------------------------------------------------------------------

function rstrip_nl(s)
    return s:gsub("\n$", "")
end

function dollar(args, func)
    local t = utils.subprocess{args=args}
    if t.error then
        msg.error("mp.utils.subprocess() returned error: " .. t.error)
    elseif t.status ~= 0 then
        msg.error("process exited with non-zero code: " .. t.status)
    elseif func then
        return func(t.stdout)
    else
        return t.stdout
    end
end

--------------------------------------------------------------------------------

function ofd_zenity(dir, title, filters)
    local args = {"zenity",
                  "--title=" .. title,
                  "--file-selection",
                  "--filename=" .. dir .. "/",
                 }
    for _, s in ipairs(filters) do
        table.insert(args, "--file-filter=" .. s:gsub(";", " "))
    end
    return dollar(args, rstrip_nl)
end

function ofd_powershell(dir, title, filters)
    local function ps_esc(s)
        return "'" .. s:gsub("'", "''") .. "'" -- needs testing
    end
    return dollar{'powershell', '-NoProfile', '-Command', [[& {
Trap {
 Write-Error -ErrorRecord $_
 Exit 1
}
Add-Type -AssemblyName PresentationFramework
$u8 = [System.Text.Encoding]::UTF8
$out = [Console]::OpenStandardOutput()
$ofd = New-Object -TypeName Microsoft.Win32.OpenFileDialog
$ofd.InitialDirectory = ]] .. ps_esc(dir) .. [[

$ofd.Title = ]] .. ps_esc(title) .. [[

$ofd.Filter = ]] .. ps_esc(table.concat(filters, "|")) .. [[

If ($ofd.ShowDialog() -ne $true) {
 Exit 1
}
$u8filename = $u8.GetBytes($ofd.FileName)
$out.Write($u8filename, 0, $u8filename.Length)
}]]}
end

--------------------------------------------------------------------------------

PROPS = { pause = true, fullscreen = false, ontop = false, }
SAVED_PROPS = {}

function before_dialog()
    for k, v in pairs(PROPS) do
        SAVED_PROPS[k] = mp.get_property_native(k)
        mp.set_property_native(k, v)
    end
end

function after_dialog()
    for k, v in pairs(SAVED_PROPS) do
        mp.set_property_native(k, v)
    end
end

--------------------------------------------------------------------------------

if package.config:sub(1, 1) == "/" then
    OFD = ofd_zenity
else
    OFD = ofd_powershell
end
VIDEO_FILTER = "Video files|*.mkv;*.mp4;*.avi;*.flv;*.mov;*.qt;*.webm;*.wmv"
AUDIO_FILTER = "Audio files|*.mp3;*.aac;*.mka;*.dts;*.flac;*.ogg;*.ogm;*.ogv;*.m4a;*.ac3;*.wav;*.wma"
SUB_FILTER = "Subtitle files|*.utf;*.utf8;*.utf-8;*.idx;*.sub;*.srt;*.smi;*.rt;*.txt;*.ssa;*.aqt;*.jss;*.js;*.ass;*.mks;*.vtt;*.sup"
ALL_FILTER = "All files|*"

mp.add_key_binding("Ctrl+q", "gui_open_file", function ()
    before_dialog()
    local file = OFD(get_dir(), "Select a file to play", {VIDEO_FILTER, AUDIO_FILTER, ALL_FILTER})
    if file then
        mp.commandv("loadfile", file, "replace")
    end
    after_dialog()
end)

mp.add_key_binding("Ctrl+l", "gui_load_sub", function ()
    before_dialog()
    local file = OFD(get_dir(), "Select a subtitle to load", {SUB_FILTER, ALL_FILTER})
    if file then
        mp.commandv("sub-add", file)
    end
    after_dialog()
end)

mp.add_key_binding("Ctrl+a", "gui_load_audio", function ()
    before_dialog()
    local file = OFD(get_dir(), "Select an audio track to load", {AUDIO_FILTER, ALL_FILTER})
    if file then
        mp.commandv("audio-add", file)
    end
    after_dialog()
end)
