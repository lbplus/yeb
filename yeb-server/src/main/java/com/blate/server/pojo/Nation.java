package com.blate.server.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
@Data
@NoArgsConstructor//无参构造
@RequiredArgsConstructor//配合@NonNull表示有参构造不写name会报错
@EqualsAndHashCode(callSuper = false,of = "name")//of="name表示以name重写equals，hashcode方法
//@EqualsAndHashCode(callSuper = false)//of="name表示以name重写equals，hashcode方法
@Accessors(chain = true)
@TableName("t_nation")
@ApiModel(value="Nation对象", description="")
public class Nation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "民族")
    @Excel(name = "民族")
    @NonNull
    private String name;

}
