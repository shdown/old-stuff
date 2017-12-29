#!/usr/bin/env bash
shopt -s extglob

javafile=../src/calc/GodObject.java
datafile=godobject.data
if ! [[ -f $datafile ]]; then
    echo >&2 "E: no '$datafile' file found."
    exit 1
fi
exec 0<>"$datafile"
true > "$javafile"
exec 1<>"$javafile"

tab='    '

typeset -A typesById=() operatorPrioritiesById=() operatorAritiesById=() operatorPrecsById=()
typeset -A types_set=() codesById=() constantsHash=()
typeset -i type_enum=-1 id_enum=0 id_max

echo "/* GENERATED AUTOMATICALLY. DO NOT EDIT. */"
echo "package calc;"
echo
echo "import ral.Real;"
echo "import options.AngleUnitConverter;"
echo
echo "public class GodObject {"
echo "${tab}public final static int"

cut_off() {
	eval "$1=\${line%%[[:space:]]*}"
	line=${line#*[[:space:]]}
	line=${line##+([[:space:]])}
}

while IFS= read -r line; do
    [[ $line == '#'* || -z $line ]] && continue

    cut_off id_str
    cut_off type_str

    types_set[$type_str]=1
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
        codesById[$id_str]=$line
    elif [[ $type_str == TYPE_CONSTANT ]]; then
        cut_off value
        if [[ $value == :* ]]; then
            constantsHash[$id_str]="Real.${value#:}"
        else
            constantsHash[$id_str]="new Real(\"${value}\")"
        fi
    fi

    echo "${tab}${tab}${id_str} = ${id_enum},"
    (( id_enum++ ))
done

id_max=$id_enum

echo

for type_str in "${!types_set[@]}"; do
    echo "${tab}${tab}${type_str} = ${type_enum},"
    (( type_enum-- ))
done

echo
echo "${tab}${tab}OP_PREC_RIGHT = 1,"
echo "${tab}${tab}OP_PREC_LEFT = 2;"
echo

print_array() {
	declare -n _in=$1
    echo "${tab}public final static ${3} ${1}[] = {"
    local -i i
    for (( i = 0; i < id_max; ++i )); do
        echo "${tab}${tab}${_in[$i]:-"$2"},"
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
for id_str in "${!constantsHash[@]}"; do
    echo "${tab}${tab}case ${id_str}:"
    echo "${tab}${tab}${tab}return ${constantsHash[$id_str]};"
done
echo "${tab}${tab}default:"
echo "${tab}${tab}${tab}throw new UnknownTokenException(\"unknown constant: ID \" + id);"
echo "${tab}${tab}}"
echo "${tab}}"

echo
echo "${tab}public static void execOperatorById(int id, Real a, Real b) {"
echo "${tab}${tab}switch(id) {"
for id_str in "${!codesById[@]}"; do
    echo "${tab}${tab}case ${id_str}:"
    echo "${tab}${tab}${tab}${codesById[$id_str]}"
    echo "${tab}${tab}${tab}break;"
done
echo "${tab}${tab}default:"
echo "${tab}${tab}${tab}throw new UnknownTokenException(\"unknown operator: ID \" + id);"
echo "${tab}${tab}}"
echo "${tab}}"

echo "}"
