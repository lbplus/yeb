package com.blate.server.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@TableName("t_position")
@ApiModel(value="Position对象", description="")
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "职位",example = "测试demo")
    @Excel(name = "职位",width = 15)
    @NonNull
    private String name;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "是否启用",example = "1")
    private Boolean enabled;


}
