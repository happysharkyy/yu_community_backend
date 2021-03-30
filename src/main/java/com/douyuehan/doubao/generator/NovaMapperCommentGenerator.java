package com.douyuehan.doubao.generator;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Properties;

public class NovaMapperCommentGenerator implements CommentGenerator {
    private String beginningDelimiter = "";
    private String endingDelimiter = "";
    private boolean forceAnnotation;

    public NovaMapperCommentGenerator() {
    }

    public void addJavaFileComment(CompilationUnit compilationUnit) {
    }

    public void addComment(XmlElement xmlElement) {
    }

    public void addRootComment(XmlElement rootElement) {
    }

    public void addConfigurationProperties(Properties properties) {
        String beginningDelimiter = properties.getProperty("beginningDelimiter");
        if (StringUtility.stringHasValue(beginningDelimiter)) {
            this.beginningDelimiter = beginningDelimiter;
        }

        String endingDelimiter = properties.getProperty("endingDelimiter");
        if (StringUtility.stringHasValue(endingDelimiter)) {
            this.endingDelimiter = endingDelimiter;
        }

        String forceAnnotation = properties.getProperty("forceAnnotation");
        if (StringUtility.stringHasValue(forceAnnotation)) {
            this.forceAnnotation = forceAnnotation.equalsIgnoreCase("TRUE");
        }

    }

    public String getDelimiterName(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(this.beginningDelimiter);
        nameBuilder.append(name);
        nameBuilder.append(this.endingDelimiter);
        return nameBuilder.toString();
    }

    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        if (markAsDoNotDelete) {
            sb.append(" do_not_delete_during_merge");
        }

        javaElement.addJavaDocLine(sb.toString());
    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
    }

    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
//            field.addJavaDocLine("/**");
//            StringBuilder sb = new StringBuilder();
//            sb.append(" * ");
//            sb.append(introspectedColumn.getRemarks());
//            field.addJavaDocLine(sb.toString());
//            field.addJavaDocLine(" */");

            StringBuilder sb = new StringBuilder();
            sb.append("@ApiModelProperty(\"");
            sb.append(introspectedColumn.getRemarks());
            sb.append("\")");
            field.addJavaDocLine(sb.toString());
        }
        if ("TIMESTAMP".equals(introspectedColumn.getJdbcTypeName()))
            field.addJavaDocLine("@JsonFormat(timezone = \"GMT+8\", pattern = \"yyyy-MM-dd HH:mm:ss\")");

        if (field.isTransient()) {
            field.addAnnotation("@Transient");
        }

        Iterator var7 = introspectedTable.getPrimaryKeyColumns().iterator();

        while (var7.hasNext()) {
            IntrospectedColumn column = (IntrospectedColumn) var7.next();
            if (introspectedColumn == column) {
                field.addAnnotation("@Id");
                field.addAnnotation("@GeneratedValue(generator = \"JDBC\")");
                break;
            }
        }

        String column = introspectedColumn.getActualColumnName();
        if (StringUtility.stringContainsSpace(column) || introspectedTable.getTableConfiguration().isAllColumnDelimitingEnabled()) {
            column = introspectedColumn.getContext().getBeginningDelimiter() + column + introspectedColumn.getContext().getEndingDelimiter();
        }

        if (!column.equals(introspectedColumn.getJavaProperty())) {
            field.addAnnotation("@Column(name = \"" + this.getDelimiterName(column) + "\")");
        } else if (!StringUtility.stringHasValue(this.beginningDelimiter) && !StringUtility.stringHasValue(this.endingDelimiter)) {
            if (this.forceAnnotation) {
                field.addAnnotation("@Column(name = \"" + this.getDelimiterName(column) + "\")");
            }
        } else {
            field.addAnnotation("@Column(name = \"" + this.getDelimiterName(column) + "\")");
        }

        if (introspectedColumn.isIdentity()) {
            if (introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement().equals("JDBC")) {
                field.addAnnotation("@GeneratedValue(generator = \"JDBC\")");
            } else {
                field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
            }
        } else if (introspectedColumn.isSequenceColumn()) {
            String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
            String sql = MessageFormat.format(introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement(), tableName, tableName.toUpperCase());
            field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY, generator = \"" + sql + "\")");
        }

    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
    }

    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    }

    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
    }

    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            sb.append(" * 获取");
            sb.append(introspectedColumn.getRemarks());
            method.addJavaDocLine(sb.toString());
            method.addJavaDocLine(" *");
        }

        sb.setLength(0);
        sb.append(" * @return ");
        sb.append(introspectedColumn.getActualColumnName());
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            sb.append(" - ");
            sb.append(introspectedColumn.getRemarks());
        }

        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */");
    }

    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            sb.append(" * 设置");
            sb.append(introspectedColumn.getRemarks());
            method.addJavaDocLine(sb.toString());
            method.addJavaDocLine(" *");
        }

        Parameter parm = (Parameter) method.getParameters().get(0);
        sb.setLength(0);
        sb.append(" * @param ");
        sb.append(parm.getName());
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            sb.append(" ");
            sb.append(introspectedColumn.getRemarks());
        }

        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */");
    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
    }
}
