`gt` does not work anymore. Move along.

All the commits but the following are made by me.

    commit 661b691e5810e6b00f308a8fb67ad6dfcd9fd903
    Author: siikamiika <siikamiika@users.noreply.github.com>
    Date:   Thu Aug 27 01:55:37 2015 +0300

        gt_notify: add --original
        
        Shows the original text that was translated.

    diff --git a/gt_notify b/gt_notify
    index b4712c3..d305c15 100755
    --- a/gt_notify
    +++ b/gt_notify
    @@ -66,6 +66,9 @@ map the "see also" list to notification actions.
         "yes": map all;
         <number>: map first <number> entries only.''')
     
    +    parser.add_argument('-o', '--original', action='store_true',
    +                        help='show original text')
    +
         parser.add_argument('-L', '--interface-lang',
                             help='specify the interface language (that is, language'
                                  ' of speech part names)')
    @@ -112,6 +115,10 @@ map the "see also" list to notification actions.
     
             summary, message = '', ''
     
    +        if args.original:
    +            summary += '{}\n'.format(
    +                html_escape(text))
    +
             if args.source_lang == 'auto':
                 summary += '(Language detected: {})'.format(
                     html_escape(translation.source_lang))
    @@ -150,7 +157,7 @@ map the "see also" list to notification actions.
                 for index, word in enumerate(see_also):
                     notification.add_action('sa_{}'.format(index), word, callback)
     
    -        notification.update(summary, message)
    +        notification.update(summary.strip(), message.strip())
             notification.show()
     
             if messing_with_mainloop:

Text that used to be here:

> # What’s that?
>
> * A library that parses Google Translate response intended for evaluation as JavaScript. Its features include:
>
>     * Transcription/transliteration
>
>     * Word translation variants by part of speech, translation variants of text segments
>
>     * Usage examples, definitions, synonyms, “see also” list
>
>     * Language detection and original language suggestion
>
>     * Typo correction
>
> * A nice command-line interface (`gt_console`) with interactive shell mode support;
>
> * An xsel+libnotify interface (`gt_notify`) that translates the content of X selection and then shows a notification with translation;
>
> * A simple debug tool (`gt_dump_json`) that dumps Google Translate response as well-formatted JSON.
>
> The `gt_languages` script can be used for fetching the abbreviation/language list (or you can just see [the table](https://github.com/shdown/gt/wiki/Languages)).
>
> The `gt_play` script can be used for playing back/downloading voice using Google Translate voice synthesizer.
