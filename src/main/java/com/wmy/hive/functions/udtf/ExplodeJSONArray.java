package com.wmy.hive.functions.udtf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.hive.functions.udtf
 * @Author: wmy
 * @Date: 2021/9/8
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 数仓中的案例，数组中有多个json
 * 1、打包上传到hdfs
 * 2、创建函数create function explode_json_array as 'com.wmy.hive.functions.udtf.ExplodeJSONArray' using jar 'hdfs://flink04:9820/user/hive/jars/flinkDemo-version-01-jar-with-dependencies.jar';
 * @Version: wmy-version-01
 */
public class ExplodeJSONArray extends GenericUDTF {

    @Override
    public StructObjectInspector initialize(ObjectInspector[] argOIs) throws UDFArgumentException {

        // 1、参数合法检查
        if (argOIs.length != 1) {
            throw new UDFArgumentException("explode_json_array 只需要一个参数");
        }

        // 2、判断参数是否是基本类型的
        if (argOIs[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
            throw new UDFArgumentException("explode_json_array 只接受基础类型参数");
        }

        // 3、将参数类型的检查器转换为基础类型对象检查其
        PrimitiveObjectInspector argumentOI = (PrimitiveObjectInspector)argOIs[0];

        // 4、判断参数是否是String类型的
        if (argumentOI.getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING) {
            throw new UDFArgumentException("explode_json_array 只接受string类型的参数");
        }

        // 5、定义返回值名称和类型
        List<String> fieldsName = new ArrayList<String>();
        fieldsName.add("action");

        List<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldsName, fieldOIs);
    }

    @Override
    public void process(Object[] objects) throws HiveException {
        // 1、获取传入的数据
        String jsonArray = objects[0].toString();

        // 2、将string转换为json数组
        JSONArray actions = new JSONArray(jsonArray);

        // 3、循环一次，取除数组中的一个json，并写出
        for (int i = 0; i < actions.length(); i++) {
            String[] result = {actions.getString(i)};
            forward(result);
        }
    }

    @Override
    public void close() throws HiveException {

    }
}
