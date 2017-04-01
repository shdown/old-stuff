#!/usr/bin/env python3
from xml.etree.cElementTree import ElementTree, Element, SubElement
from xml.sax.saxutils import escape as xml_escape
import sys
import argparse
import vk

class Entry:
    # domain is either None or str
    # oid is either None or int
    def __init__(self, domain=None, oid=None):
        assert(domain is not None or oid is not None)
        self.domain = domain
        self.oid = oid

    def __eq__(self, that):
        if self.domain is not None and self.domain == that.domain:
            return True
        return self.oid == that.oid

DOMAIN_PREFIXES = [
    [1,  ['id']],
    [-1, ['public', 'club']],
]
def entry_from_regular_domain(domain):
    for signum, prefixes in DOMAIN_PREFIXES:
        for prefix in prefixes:
            if domain.startswith(prefix):
                num = domain[len(prefix):]
                if num.isdigit():
                    return Entry(oid=signum * int(num))
    return Entry(domain=domain)

def entry_to_request_kwarg(entry):
    if entry.oid is not None:
        return dict(owner_id=entry.oid)
    return dict(domain=entry.domain)

def entry_from_obj(obj, is_group):
    signum = -1 if is_group else 1
    return Entry(domain=obj.get('screen_name'), oid=signum * obj['id'])

def entry_to_regular_domain(entry):
    if entry.domain is not None:
        return entry.domain
    if entry.oid < 0:
        return 'public{}'.format(-entry.oid)
    return 'id{}'.format( entry.oid)

def generate(session, entry, count, max_post_title_len, name_format):
    def _as_text(p, wrap_left=''):
        r = ''
        if p['post_type'] != 'post':
            r += '[%s]\n' % p['post_type']
        if p.get('is_pinned'):
            r += '[pinned]\n'
        if 'from_id' in p and p['from_id'] != owner_id:
            r += '[posted by %s]\n' % names.get(p['from_id'], '')
        r += p['text']
        for q in p.get('copy_history', []):
            r += '\n'
            r += _as_text(q, wrap_left='> '+wrap_left)
            r += '\n'
        for a in p.get('attachments', []):
            t = a['type']
            a = a[t] # [sic]
            if t == 'video':
                r += '\n[has video attachment "%s"]' % a.get('title', '')
            elif t == 'audio':
                r += '\n[has audio attachment "%s - %s"]' % (a.get('performer', ''), a.get('title', ''))
            elif t == 'doc':
                r += '\n[has document attachment "%s"]' % a.get('title', '')
            elif t == 'link':
                r += '\n[has link attachment: "%s"]' % a.get('url', '')
            elif t == 'note':
                r += '\n[has note attachment: "%s"]' % a.get('title', '')
            elif t == 'app':
                r += '\n[has application content attachment: "%s"]' % a.get('app_name', '')
            elif t == 'poll':
                r += '\n[has poll: "%s"]' % a.get('question', '')
            elif t == 'page':
                r += '\n[has wiki page attachment: "%s"]' % a.get('title', '')
            else:
                r += '\n[has attachment of type %s]' % t
        if 'geo' in p:
            r += '\n[has location info]'
        if 'signer_id' in p:
            r += '\n[signed by %s]' % names.get(p['signer_id'], '')
        return wrap_left + r.replace('\n', '\n'+wrap_left)
    resp = session.request('wall.get', v='5.2', count=count, extended=1, **entry_to_request_kwarg(entry))
    owner_id = None
    names = {}
    for obj in resp['profiles']:
        e = entry_from_obj(obj, is_group=False)
        names[e.oid] = obj.get('first_name', '') + ' ' + obj.get('last_name', '')
        if e == entry:
            owner_id = e.oid
    for obj in resp['groups']:
        e = entry_from_obj(obj, is_group=True)
        names[e.oid] = obj.get('name', '')
        if e == entry:
            owner_id = e.oid
    rss = Element('rss', version='2.0')
    chan = SubElement(rss, 'channel')
    SubElement(chan, 'title').text = name_format % names.get(owner_id, '')
    regular_domain = entry_to_regular_domain(entry)
    SubElement(chan, 'link').text = 'https://vk.com/' + regular_domain
    SubElement(chan, 'description').text = '%s\'s wall' % regular_domain
    for obj in resp['items']:
        link = 'https://vk.com/wall%d_%d' % (obj['to_id'], obj['id'])
        text = _as_text(obj)
        item = SubElement(chan, 'item')
        SubElement(item, 'title').text = text.replace('\n', ' ')[:max_post_title_len]
        SubElement(item, 'link').text = link
        SubElement(item, 'description').text = xml_escape(text).replace('\n', '<br>')
        SubElement(item, 'guid').text = link
        # How do we know Moscow timezone?
        #SubElement(item, 'pubDate').text = magic(obj['date'])
    et = ElementTree(rss)
    et.write(sys.stdout, encoding='unicode', xml_declaration=True)

def main():
    p = argparse.ArgumentParser()
    p.add_argument('--count', '-n', type=int, default=20)
    p.add_argument('--access-token', default=None)
    p.add_argument('--max-post-title-len', type=int, default=120)
    p.add_argument('--name-format', default='%.120s')
    p.add_argument('domain')
    a = p.parse_args()
    session = vk.Session(defparams=dict(access_token=a.access_token or ''))
    generate(session=session,
             entry=entry_from_regular_domain(a.domain),
             count=a.count,
             max_post_title_len=a.max_post_title_len,
             name_format=a.name_format)

if __name__ == '__main__':
    main()
