package CreateSQLParser.Parser;

import CreateSQLParser.Lex.Word;
import DataCreater.templet.CustomStringtypeConfigLoader;
import SavingTypeString.DataType;

import java.util.HashSet;
import java.util.Set;

public class Fugue {
    Set<String> mark;
    Set<String> data;
    Set<String> multi;
    CustomStringtypeConfigLoader cscl;

    public void init() {
        mark = new HashSet<>();
        mark.add("create");
        mark.add("table");
        mark.add("{");
        mark.add("}");
        mark.add("(");
        mark.add(")");
        mark.add(",");
        mark.add("null");
        mark.add("default");
        mark.add("key");
        mark.add("unique");
        mark.add("stringtype");
        mark.add("numberarea");
        mark.add("regulartype");
        mark.add("unmake");
        mark.add(";");

        data = DataType.getTypeKeySet();

        multi = new HashSet<>();
        multi.add("not");
        multi.add("primary");
        multi.add("foreign");
        multi.add("references");
    }

    public Fugue() {
        init();
    }

    public Word[] fugue(Word[] words) {
        boolean inlineOpen = false;
        if (!words[0].getName().equals("create")) {
            throw new RuntimeException("not start with create");
        }

        Word w = words[0], last;
        for (int loop = 0; loop<words.length-1; loop++) {
            last = w;
            w = words[loop+1];
            if (data.contains(w.getName())) {
                w.setSubstance(DataType.getDataTypeString(w.getName()));
                w.setName("type");
            } else if (multi.contains(w.getName())) {
                continue;
            } else if (mark.contains(w.getName())) {
                if (w.getName().equals("{"))
                    inlineOpen = true;
                else if (w.getName().equals("}"))
                    inlineOpen = false;
                continue;
            } else {
                if (inlineOpen) {
                    if (!w.getName().equals("String"))
                        w.setSubstance(w.getName());
                    w.setName("inline");
                } else if (checknumber(w.getName())) {
                    w.setSubstance(w.getName());
                    switch (last.getName()) {
                        case "default":
                            w.setName("defaultStr");
                            break;
                        case "numberarea":
                        case "~":
                            w.setName("numberange");
                            break;
                        default:
                            w.setName("range");
                            break;
                    }
                } else switch (last.getName()) {
                    case "table":
                        if (!w.getName().equals("String"))
                            w.setSubstance(w.getName().toLowerCase());
                        w.setName("tablename");
                        break;
                    case "(":
                        if(words[loop-2].getName().equals("references")){
                            w.setSubstance(w.getName().toLowerCase());
                            w.setName("listname");
                            break;
                        }
                    case ",":
                        if (!w.getName().equals("String"))
                            w.setSubstance(w.getName());
                        w.setName("listname");
                        break;
                    case "default":
                        if (!w.getName().equals("String"))
                            w.setSubstance(w.getName());
                        w.setName("defaultStr");
                        break;
                    case "stringtype":
                        if(CustomStringtypeConfigLoader.getCustomStringtypes().containsKey(w.getName())){
                            w.setSubstance(CustomStringtypeConfigLoader.getCustomStringtypes().getProperty(w.getName()));
                            w.setName("isRegular");
                        }
                        else {
                            w.setSubstance(w.getName());
                            w.setName("defaultdatatype");
                        }
                        break;
                    case "regulartype":
                        w.setSubstance(w.getName());
                        w.setName("isRegular");
                        break;
                    case "references":
                        w.setSubstance(w.getName().toLowerCase());
                        w.setName("tablename");
                        break;
                    default:
                        break;
                }
            }
        }
        return words;
    }

    private boolean checknumber(String s) {
        int fu = 0;
        for (char c : s.toCharArray()) {
            if (fu == 0 && c == '-')
                fu++;
            else if ((c < 48 || c > 57) && c!='.')
                return false;
            else
                fu++;
        }
        return true;
    }
}
