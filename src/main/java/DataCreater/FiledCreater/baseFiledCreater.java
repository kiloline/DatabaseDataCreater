package DataCreater.FiledCreater;

import Utils.StringSpecificationOutput;
import Utils.env_properties;
import dataStructure.ListStructure;
import dataStructure.TableStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class baseFiledCreater {
    protected TableStructure tableStructure;
    private Map<String, List<String>> unique;

    public baseFiledCreater(TableStructure tableStructure) {
        this.tableStructure = tableStructure;
        this.unique = new HashMap<>();
        for (ListStructure ls : tableStructure.getStruc()) {
            if (ls.isSingal()) {
                unique.put(ls.getListname(), new ArrayList<String>());
            }
        }
    }


    /**
     * 生成对应数据形式的插入内容
     *
     * @return
     * @throws Exception
     */
    public String makeinsert(int makenum) {
        StringBuilder Return = new StringBuilder(packHead(tableStructure.isUnmake()));

        for (int loop = 0; loop < makenum; loop++) {
            try {
                packFiled(tableStructure, Return);
            } catch (Exception e) {
                System.out.println(Return.toString());
                throw new RuntimeException(e.getMessage());
            }
        }

        Return.deleteCharAt(Return.length() - 1);
        String insert = Return.append(packTail()).append("\n").toString();
        return new String(insert.getBytes(), env_properties.getEnvirmentCharset());
    }

    protected String strSpecification(ListStructure ls, String appendStr) {
        switch (ls.getListType()) {
            case "decimal":
            case "int":
                return StringSpecificationOutput.specNumber(appendStr, ls.getRange()[0], ls.getRange()[1]);
            case "bool":
                return StringSpecificationOutput.specBool(appendStr);
            case "string":
                return StringSpecificationOutput.specString(
                        appendStr, ls.getRange()[0]);
            case "date":
                return appendStr;
            default:
                return null;
        }
    }

    /**
     * 用于唯一约束检测，每一个线程会分配一个
     *
     * @param ls
     * @param appendStr
     */
    protected boolean addtoSet(ListStructure ls, String appendStr) {
        if (ls.isSingal()) {
            if (unique.get(ls.getListname()).contains(appendStr)) {
                return false;//错误标记，用于一旦出现了重复的字段内容的时候重新生成数据
            } else {
                unique.get(ls.getListname()).add(appendStr);
            }
        }
        return true;
    }

    protected String makeFiled(ListStructure ls) throws ClassNotFoundException {
        if (!ls.getListType().equals("string")) {
            if (ls.isAdvancedType())
                throw new ClassNotFoundException("非字符串类型不能使用stringtype关键字");
            else if (ls.isRegular())
                    throw new ClassNotFoundException("非字符串类型不能使用regulartype关键字");
        }
//        else if ((!ls.isSingal()) && ls.isDefault() && privateRandom.RandomDouble(0, 1) <= 2 * Double.valueOf(env_properties.getEnvironment("defaultProportion"))) {
//            return ls.getDefaultStr();//当存在类似唯一约束的情况时将屏蔽默认值
//        }
        return ls.getString();
    }

    /**
     * 依赖于不同的数据格式，一条数据插入时候的头部特殊格式
     *
     * @param isUnmake
     * @return
     */
    protected abstract String packHead(boolean isUnmake);

    /**
     * 依赖于不同的数据格式，各类型字段的特殊格式
     *
     * @param table
     * @param out
     */
    protected abstract void packFiled(TableStructure table, StringBuilder out) throws Exception;

    /**
     * 依赖于不同的数据格式，一条数据插入时候的尾部特殊格式
     *
     * @return
     */
    protected abstract String packTail();
}