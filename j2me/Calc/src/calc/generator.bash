#!/usr/bin/env bash

if ! [[ -f GodObject.data ]]; then
    echo >&2 "E: no 'GodObject.data' in current directory."
    exit 1
fi
exec 0<>GodObject.data
true > GodObject.java
exec 1<>GodObject.java

tab='    '

unset typesById operatorPrioritiesById operatorAritiesById operatorPrecsById \
      fields id_num id_max type_enum types_hash codes_hash

typeset -a typesById operatorPrioritiesById operatorAritiesById operatorPrecsById
typeset -a fields
typeset -i id_enum=0 id_max type_enum=-1
typeset -A types_hash codes_hash constants_hash

echo "/* GENERATED AUTOMATICALLY. DO NOT EDIT. */"
echo "package calc;"
echo
echo "import ral.Real;"
echo
echo "public class GodObject {"
echo "${tab}public final static int"

cut_off() {
    eval "$1=\${line%% *}"
    line=${line#* }
    while [[ $line == ' '* ]]; do
        line=${line# }
    done
}

while IFS= read -r line; do
    [[ $line == '#'* || -z $line ]] && continue

    cut_off id_str
    cut_off type_str

    types_hash[$type_str]=1
    typesById[$id_enum]=$type_str

    if [[ $type_str == TYPE_OP ]]; then
        cut_off 'operatorPrioritiesById[$id_enum]'
        cut_off 'operatorAritiesById[$id_enum]'
        cut_off prec
        case "$prec" in
            R) operatorPrecsById[$id_enum]=OP_PREC_RIGHT ;;
            L) operatorPrecsById[$id_enum]=OP_PREC_LEFT ;;
            *) operatorPrecsById[$id_enum]="-1 /* unknown prec '$prec' */" ;;
        esac
        codes_hash[$id_str]=$line
    elif [[ $type_str == TYPE_PRE_CONSTANT ]]; then
        cut_off value
        if [[ $value == :* ]]; then
            constants_hash[$id_str]="Real.${value#:}"
        else
            constants_hash[$id_str]="new Real(\"${value}\")"
        fi
    fi

    echo "${tab}${tab}${id_str} = ${id_enum},"
    (( id_enum++ ))
done

id_max=$id_enum

echo

for type_str in "${!types_hash[@]}"; do
    echo "${tab}${tab}${type_str} = ${type_enum},"
    (( type_enum-- ))
done

echo
echo "${tab}${tab}OP_PREC_RIGHT = 1,"
echo "${tab}${tab}OP_PREC_LEFT = 2;"
echo

print_array() {
    echo "${tab}public final static ${3} ${1}[] = {"
    local -i i
    local val
    for (( i = 0; i < id_max; ++i )); do
        eval "val=\${${1}[$i]}"
        echo "${tab}${tab}${val:-"$2"},"
    done
    echo "${tab}};"
}

print_array typesById '0 /* what? */' int
for array in operatorPrioritiesById operatorAritiesById operatorPrecsById; do
    print_array "$array" '-1 /* not an operator */' int
done

echo
echo "${tab}public static Real getConstantById(int id) {"
echo "${tab}${tab}switch(id) {"
for id_str in "${!constants_hash[@]}"; do
    echo "${tab}${tab}case ${id_str}:"
    echo "${tab}${tab}${tab}return ${constants_hash[$id_str]};"
done
echo "${tab}${tab}default:"
echo "${tab}${tab}${tab}throw new UnknownTokenException(\"unknown constant: ID \" + id);"
echo "${tab}${tab}}"
echo "${tab}}"

echo
echo "${tab}public static void execOperatorById(int id, Real a, Real b) {"
echo "${tab}${tab}switch(id) {"
for id_str in "${!codes_hash[@]}"; do
    echo "${tab}${tab}case ${id_str}:"
    echo "${tab}${tab}${tab}${codes_hash[$id_str]}"
    echo "${tab}${tab}${tab}break;"
done
echo "${tab}${tab}default:"
echo "${tab}${tab}${tab}throw new UnknownTokenException(\"unknown operator: ID \" + id);"
echo "${tab}${tab}}"
echo "${tab}}"

echo "}"