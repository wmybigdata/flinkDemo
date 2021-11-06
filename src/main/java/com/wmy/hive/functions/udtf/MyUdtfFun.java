package com.wmy.hive.functions.udtf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.hive.functions.udtf
 * @Author: wmy
 * @Date: 2021/9/17
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: hive 的UDTF的实现
 * @Version: wmy-version-01
 */
@Description(name = "myudtf_01", value = "2021-09-17 wmy udtf",
        extended = "Example: \n" +
                "> select hobby1,hobby2 from wmy_udtf lateral view myudtf_01(hobby)  my_table as hobby1,hobby2;"
)
public class MyUdtfFun extends GenericUDTF {


    /**
     * initialize：主要是初始化返回的列和返回的列类型
     * @param argOIs
     * @return
     * @throws UDFArgumentException
     */
    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {
        // 设置新的列名
        ArrayList<String> columns = new ArrayList<>();
        //有几列，添加几列
        columns.add("column1");
        columns.add("column2");

        // 设置每列类型，有几列就设置几列的类型
        ArrayList<ObjectInspector> columnType = new ArrayList<>();
        columnType.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        columnType.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        // 返回所需要的列名和列的类型
        return ObjectInspectorFactory.getStandardStructObjectInspector(columns,columnType);
    }

    /**
     * process：方法对输入的每一行进行操作，他通过调用forward()返回一行或者多行数据
     * @param objects
     * @throws HiveException
     */
    @Override
    public void process(Object[] objects) throws HiveException {
        // 从一列变成一行输出，得到的是一个数组，所以有多行数据遍历
        String[] fields = objects[0].toString().split(",");
        for (int i = 0; i < fields.length; i += 2) {
            forward(new Object[]{fields[i], fields[i + 1]});
        }
    }

    /**
     * close：在process方法结束后调用，用于进行一些其他的操作，只执行一次
     * @throws HiveException
     */
    @Override
    public void close() throws HiveException {

    }
}
