STD_CFLAGS := -std=c99 -pedantic
WARNING_CFLAGS := -Wall -Wextra -Wlogical-op -Wconversion -Wfloat-equal \
                  -Wredundant-decls -Wshadow -Wpointer-arith -Winline \
                  -Wdisabled-optimization -Wstrict-prototypes

ifneq ($(shell pkg-config --exists alsa && echo 1 || echo 0),1)
    $(error no 'alsa' library found)
endif
ALSA_CFLAGS := $(shell pkg-config --cflags alsa)
ALSA_LIBS := $(shell pkg-config --libs alsa)

CPPFLAGS :=
LDFLAGS := -Wl,--strip-all
CFLAGS := $(STD_CFLAGS) $(WARNING_CFLAGS) -Os $(ALSA_CFLAGS)
LIBS := $(ALSA_LIBS)

alsawatch: alsawatch.c
	$(CC) $(CPPFLAGS) $(CFLAGS) $(LDFLAGS) $^ -o $@ $(LIBS)

clean:
	$(RM) alsawatch

.phony: clean
