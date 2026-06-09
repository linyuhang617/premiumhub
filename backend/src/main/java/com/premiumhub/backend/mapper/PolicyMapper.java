package com.premiumhub.backend.mapper;

import com.premiumhub.backend.dto.PolicyQueryRequest;
import com.premiumhub.backend.entity.Policy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PolicyMapper {
    List<Policy> findByCondition(@Param("policyNo") String policyNo,
                                  @Param("status") String status,
                                  @Param("dueDateFrom") String dueDateFrom,
                                  @Param("dueDateTo") String dueDateTo,
                                  @Param("sortColumn") String sortColumn,
                                  @Param("sortDir") String sortDir,
                                  @Param("size") int size,
                                  @Param("offset") int offset);

    long countByCondition(@Param("policyNo") String policyNo,
                           @Param("status") String status,
                           @Param("dueDateFrom") String dueDateFrom,
                           @Param("dueDateTo") String dueDateTo);
}
