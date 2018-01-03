#define _POSIX_C_SOURCE 200809L // getopt
#include <anitomy/anitomy.h>
#include <vector>
#include <algorithm>
#include <iostream>
#include <cstdlib> // exit, EXIT_SUCCESS, EXIT_FAILURE
#include <unistd.h> // getopt
#include <cstring> // strcmp

static bool extract_season = false, extract_volume = false;
static wchar_t delimiter = L'\n';

class Info {
private:
    std::vector<std::wstring> episode_;
    std::vector<std::wstring> season_;
    std::vector<std::wstring> volume_;

    static int compare_wstr_vectors_(
        const std::vector<std::wstring> &a,
        const std::vector<std::wstring> &b)
    {
        const size_t na = a.size();
        const size_t nb = b.size();
        const size_t nmin = std::min(na, nb);
        for(size_t i = 0; i < nmin; ++i) {
            if(int r = a[i].compare(b[i])) {
                return r;
            }
        }
        return na < nb ? -1 : (na > nb ? 1 : 0);
    }

public:
    Info(const anitomy::Elements &elems)
        : episode_(elems.get_all(anitomy::kElementEpisodeNumber))
        , season_()
        , volume_()
    {
        if(extract_season) {
            season_ = elems.get_all(anitomy::kElementAnimeSeason);
        }
        if(extract_volume) {
            volume_ = elems.get_all(anitomy::kElementVolumeNumber);
        }
    }

    operator bool() const {
        return
            !episode_.empty() ||
            !season_.empty() ||
            !volume_.empty();
    }

    bool operator <(const Info &that) const {
        if(int r = compare_wstr_vectors_(volume_, that.volume_)) {
             return r < 0;
        }
        if(int r = compare_wstr_vectors_(season_, that.season_)) {
             return r < 0;
        }
        if(int r = compare_wstr_vectors_(episode_, that.episode_)) {
             return r < 0;
        }
        return false;
    }

    bool operator ==(const Info &that) const {
        return
            episode_ == that.episode_ &&
            season_ == that.season_ &&
            volume_ == that.volume_;
    }
};

static std::wstring wbasename(const std::wstring &s) {
    const size_t i = s.find_last_not_of(L"/");
    if(i == std::wstring::npos) {
        return s.empty() ? L"." : L"/";
    }
    const size_t from = 1 + s.find_last_of(L"/", i); /* std::wstring::npos + 1 == 0 */
    return s.substr(from, i - from + 1);
}

static bool do_attach() {
    anitomy::Anitomy anitomy;
    anitomy.options().parse_episode_title = false;
    anitomy.options().parse_file_extension = false;
    anitomy.options().parse_release_group = false;

    std::vector<std::wstring> videos;
    for(std::wstring line; std::getline(std::wcin, line, delimiter) && !line.empty();) {
        videos.emplace_back(line);
    }

    std::vector<std::pair<Info,std::wstring>> subs;
    for(std::wstring line; std::getline(std::wcin, line, delimiter) && !line.empty();) {
        if(!anitomy.Parse(wbasename(line))) {
            continue;
        }
        Info info(anitomy.elements());
        if(!info) {
            continue;
        }
        subs.emplace_back(std::move(info), line);
    }
    if(std::wcin) {
        std::cerr << "ERROR: extra data\n";
        return false;
    }

    std::sort(subs.begin(), subs.end());

    bool first = true;
    for(const auto &video : videos) {
        if(!first) {
            std::wcout << delimiter;
        }
        std::wcout << video << delimiter;
        first = false;

        if(!anitomy.Parse(wbasename(video))) {
            continue;
        }
        Info info(anitomy.elements());
        if(!info) {
            continue;
        }
        std::pair<Info,std::wstring> p(
            std::move(info),
            std::wstring() // empty string is always less or equal to anything
        );
        for(auto it = std::lower_bound(subs.begin(), subs.end(), p);
            it != subs.end() && it->first == p.first;
            ++it)
        {
            std::wcout << it->second << delimiter;
        }
    }

    return true;
}

static bool do_sort() {
    anitomy::Anitomy anitomy;
    anitomy.options().parse_episode_title = false;
    anitomy.options().parse_file_extension = false;
    anitomy.options().parse_release_group = false;

    std::vector<std::pair<Info,std::wstring>> parsed;
    std::vector<std::wstring> unparseable;
    for(std::wstring line; std::getline(std::wcin, line, delimiter);) {
        if(!anitomy.Parse(wbasename(line))) {
            unparseable.emplace_back(line);
            continue;
        }
        parsed.emplace_back(Info(anitomy.elements()), line);
    }
    std::sort(parsed.begin(), parsed.end());
    for(const auto &line : unparseable) {
        std::wcout << line << delimiter;
    }
    for(const auto &p : parsed) {
        std::wcout << p.second << delimiter;
    }
    return true;
}

static void usage() {
    std::cerr << "USAGE: anitomy-cli [-S] [-V] [-z] {sort | attach}\n";
    exit(EXIT_FAILURE);
}

int main(int argc, char **argv) {
    for(int c; (c = getopt(argc, argv, "SVz")) != -1;) {
        switch(c) {
        case 'S':
            extract_season = true;
            break;
        case 'V':
            extract_volume = true;
            break;
        case 'z':
            delimiter = L'\0';
            break;
        default:
            usage();
            break;
        }
    }

    // This should go after getopt() because it prints error messages to stderr.
    std::ios::sync_with_stdio(NULL);
    std::wcin.tie(NULL);
    std::wcout.tie(NULL);

    int nposarg = argc - optind;
    if(nposarg != 1) {
        usage();
    }
    const char *action = argv[optind];
    if(strcmp(action, "sort") == 0) {
        return do_sort() ? EXIT_SUCCESS : EXIT_FAILURE;
    } else if(strcmp(action, "attach") == 0) {
        return do_attach() ? EXIT_SUCCESS : EXIT_FAILURE;
    } else {
        usage();
    }
}
