#include <alsa/asoundlib.h>
#include <stdio.h>
#include <alloca.h>

#define PROGNAME        "alsawatch"

#define DEFAULT_CARD    "default"
#define DEFAULT_CHANNEL "Master"

#define TRY(expr)         \
    do {                  \
        int ret = (expr); \
        if(ret < 0)       \
            return ret;   \
    } while(0)

#define CHECK(expr)                                                   \
    do {                                                              \
        int ret = (expr);                                             \
        if(ret < 0) {                                                 \
            fprintf(stderr, "E: %s: %s\n", #expr, snd_strerror(ret)); \
            exit(EXIT_FAILURE);                                       \
        }                                                             \
    } while(0)

void *xmalloc(size_t size) {
    void *res = malloc(size);
    if(!res) {
        fprintf(stderr, "E: malloc(%zu) failed.\n", size);
        exit(EXIT_FAILURE);
    }
    return res;
}

char *get_card_name(const char *nice_name) {
    snd_ctl_card_info_t *info = NULL;
    snd_ctl_card_info_alloca(&info);

    const size_t buf_size = 16;
    char *buf = xmalloc(buf_size);

    int card_number = -1;

    while(snd_card_next(&card_number) == 0 && card_number != -1) {
        snprintf(buf, buf_size, "hw:%d", card_number);
        snd_ctl_t *ctl = NULL;
        if(snd_ctl_open(&ctl, buf, 0) < 0) {
            continue;
        }
        if(snd_ctl_card_info(ctl, info) < 0) {
            snd_ctl_close(ctl);
            continue;
        }
        snd_ctl_close(ctl);
        const char *card_nice_name = snd_ctl_card_info_get_name(info);
        if(strcmp(card_nice_name, nice_name) == 0)
            return buf;
    }
    free(buf);
    return NULL;
}

int open_card(snd_mixer_t **mixer, const char *card_name) {
    TRY(snd_mixer_open(mixer, 0));
    TRY(snd_mixer_attach(*mixer, card_name));
    TRY(snd_mixer_selem_register(*mixer, NULL, NULL));
    TRY(snd_mixer_load(*mixer));
    return 0;
}

void open_channel(snd_mixer_elem_t **elem, snd_mixer_t *mixer,
        const char *channel_name)
{
    snd_mixer_selem_id_t *sid;
    snd_mixer_selem_id_alloca(&sid);
    snd_mixer_selem_id_set_name(sid, channel_name);
    *elem = snd_mixer_find_selem(mixer, sid);
}

enum mute_state { MUTED, UNMUTED, NO_MUTE_SWITCH };

int get_mute_state(enum mute_state *out, snd_mixer_elem_t *elem) {
    if(!snd_mixer_selem_has_playback_switch(elem)) {
        *out = NO_MUTE_SWITCH;
        return 0;
    }
    int pswitch;
    TRY(snd_mixer_selem_get_playback_switch(elem, 0, &pswitch));
    *out = pswitch ? UNMUTED : MUTED;
    return 0;
}

int get_volume(int *out, snd_mixer_elem_t *elem) {
    long pmin, pmax, value;
    TRY(snd_mixer_selem_get_playback_volume_range(elem, &pmin, &pmax));
    TRY(snd_mixer_selem_get_playback_volume(elem, 0, &value));
    /* round(100*a/b) = floor((floor(200*a/b) + 1) / 2)
     * Proof:
     *     let p = 100*a*b
     *     if frac(p) < 0.5:
     *         floor(200*a/b) = floor(2*p) = 2*p
     *         floor((2*p + 1) / 2) = floor(p)
     *     if frac(p) >= 0.5:
     *         floor(200*a/b) = floor(2*p) = 2*p + 1
     *         floor((2*p + 2) / 2) = floor(p) + 1
     */
    *out = (int)(200 * (value - pmin) / (pmax - pmin) + 1) / 2;
    return 0;
}

int print_state(snd_mixer_elem_t *elem) {
    enum mute_state mute_state;
    int volume;
    TRY(get_mute_state(&mute_state, elem));
    TRY(get_volume(&volume, elem));
    printf("%s %d\n", mute_state == MUTED ? "mute" : "unmute", volume);
    fflush(stdout);
    return 0;
}

void usage(void) {
    fprintf(stderr,
            "USAGE: %s [channel [card]]\n"
            "\n"
            "Default card is '%s'.\n"
            "Default channel is '%s'.\n", PROGNAME, DEFAULT_CARD,
                    DEFAULT_CHANNEL);
    exit(EXIT_FAILURE);
}

int main(int argc, char **argv) {
    char *card_name = DEFAULT_CARD, *channel_name = DEFAULT_CHANNEL;

    if(argc > 3) {
        usage();
    }
    if(argc > 1) {
        channel_name = argv[1];
    }
    if(argc > 2) {
        card_name = argv[2];
    }

    char *real_name = get_card_name(card_name);
    if(real_name) {
        fprintf(stderr, "I: '%s' is '%s'.\n", card_name, real_name);
        card_name = real_name;
    }

    snd_mixer_t *mixer = NULL;
    snd_mixer_elem_t *elem = NULL;
    CHECK(open_card(&mixer, card_name));
    open_channel(&elem, mixer, channel_name);
    if(elem == NULL) {
        fprintf(stderr, "E: failed to open channel '%s'.\n", channel_name);
        exit(EXIT_FAILURE);
    }

    while(1) {
        CHECK(print_state(elem));
        CHECK(snd_mixer_wait(mixer, -1));
        CHECK(snd_mixer_handle_events(mixer));
    }
    return EXIT_SUCCESS; // stub
}
