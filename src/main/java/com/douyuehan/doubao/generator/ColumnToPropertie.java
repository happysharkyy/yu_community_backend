package com.douyuehan.doubao.generator;

import java.util.ArrayList;
import java.util.List;

public class ColumnToPropertie {

    public static void main(String[] args) {
       String s = "id,dept_name,dept_level,parent_id,linkman,dept_tel,status,dept_type,default_flag,sort_no,remark,org_seq,oper,create_date,update_oper,update_date";
       String r = getFields(s).toString();
       System.out.println(r.substring(1,r.length()-1));

    }

    static List<String> getFields(String s) {
        String[] columns = s.split(",");
        List<String> list = new ArrayList<>();
        for (String column : columns) {
            String newColumn = column;

            int doIndex = column.indexOf(".");
            if (doIndex > 0) {
                newColumn = column.substring(doIndex+1);
            }
            String field = dealField(newColumn);
            if(newColumn.equals(field)){
                list.add(column);
            }else{
                list.add(column + " " +field);
            }
        }
        return list;
    }

    private static String dealField(String column) {
        String[] parts = column.split("_");
        if (parts.length == 1) {
            return column;
        }
        String field = parts[0];
        for (int i = 1; i < parts.length; i++) {
            field = field + parts[i].substring(0, 1).toUpperCase()+parts[i].substring(1).toLowerCase();
        }
        return field ;
    }

}
