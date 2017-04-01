@test_dash_starting_filenames() {
    setup -file-starts-with-dash.mkv
    local -a out
    run_get_output out
    if (( ${#out[@]} == 1 )) && [[ ${out[0]} == $PWD/-file-starts-with-dash.mkv ]]; then
        @success "OK absolute path passed"
    else
        @fail "unexpected output '${out[*]}'"
    fi
}

@test_fail_when_no_media_1() {
    setup
    run_check_fail
}

@test_fail_when_no_media_2() {
    setup sound.mp3 subtitle.ass directory.mkv/ text_file.txt file_without_extension
    run_check_fail
}

@test_alphabetic_sort() {
    setup_run_check_output {1,2,3}.mkv {a,b,c}.mp4 {а,б,в}.mkv
}

@test_nonrecursive_by_default() {
    setup file.mkv foo/file.mkv
    run_check_output file.mkv
}

@test_recursive() {
    set_arguments -r
    setup_run_check_output file.mkv foo/file.mkv foo/foo/foo/foo/file.mkv
}

@test_recursive_numsort() {
    set_arguments -r -n
    setup_run_check_output season{2,12}/episode{2,12}.mkv
}

@test_numsort() {
    set_arguments -n
    setup_run_check_output {0,2,12,9999}.mkv
}

@test_numsort_unnumered_before() {
    set_arguments -n
    setup_run_check_output no-numbers.mkv {2,12}.mkv
}

@test_numsort_no_extension_numbers() {
    set_arguments -n
    setup_run_check_output file-with-number-in-extension.mp4 {2,12}.mkv
}

@test_numsort_leading_zeroes() {
    set_arguments -n
    setup_run_check_output {00..10}.mkv
}

@test_numsort_non_numbers() {
    set_arguments -n
    write_config 'NON_NUMBERS=( "+([0-9])" )'
    setup_run_check_output season1_ep{2,12}.mkv
}

@test_numsort_equal_numbers() {
    set_arguments -n
    setup foo1.mkv bar1.mkv bar2.mkv
    local -a out
    run_get_output out
    local IFS=' '
    case "${out[*]}" in
        'foo1.mkv bar1.mkv bar2.mkv'|'bar1.mkv foo1.mkv bar2.mkv')
            @success "OK '${out[*]}'" ;;
        *)
            @fail "unexpected output '${out[*]}'" ;;
    esac
}

@test_subpaths_1() {
    set_arguments -s
    setup foo.ass foo.mkv
    run_check_output foo.mkv
}

@test_subpaths_2() {
    set_arguments -s
    setup subs/foo.ass foo.mkv
    run_check_output --sub-paths=subs foo.mkv
}

@test_subpaths_3() {
    set_arguments -s
    setup subs{1,2}/foo.ass foo.mkv
    local -a out
    run_get_output out
    local IFS=' '
    case "${out[*]}" in
        '--sub-paths=subs1:subs2 foo.mkv'|'--sub-paths=subs2:subs1 foo.mkv')
            @success "OK '${out[*]}'" ;;
        *)
            @fail "unexpected output '${out[*]}'" ;;
    esac
}

@test_subnummatch_1() {
    set_arguments -S
    setup baz1.ass foo1.ass bar1.mkv
    run_check_output --\{ --sub-file=baz1.ass --sub-file=foo1.ass bar1.mkv --\}
}

@test_subnummatch_2() {
    set_arguments -S
    setup no-number.ass no-number.mkv
    run_check_output no-number.mkv
}

@test_subnummatch_recursive() {
    set_arguments -r -S
    setup root1.ass foo/bar1.ass foo/baz1.mkv
    run_check_output --\{ --sub-file=foo/bar1.ass foo/baz1.mkv --\}
}

@test_audio_exact_1() {
    set_arguments -a
    setup bar.mkv bar.mp3
    run_check_output bar.mkv
}

@test_audio_exact_2() {
    set_arguments -a
    setup bar.mkv foo/bar.mp3
    run_check_output --\{ --audio-file=foo/bar.mp3 bar.mkv --\}
}
