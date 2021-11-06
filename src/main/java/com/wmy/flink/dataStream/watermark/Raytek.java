package com.wmy.flink.dataStream.watermark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.watermark
 * @Author: wmy
 * @Date: 2021/11/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 红外测温仪的实体类
 * @Version: wmy-version-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Raytek {
    private String id;
    private Double temperature;
    private String name;
    private Long ts;
    private String location;
}
