package win.leizhang.demo.springboot.mq.dal.customdao.dao.dsoauth2;

import org.apache.ibatis.annotations.Param;
import win.leizhang.mqcommon.activemq.model.MqCountResult;

import java.util.List;

public interface MqMapper {

    // 删除保留日期范围外的数据
    void deleteSender(@Param("keepDate") String keepDate);

    void deleteReceiver(@Param("keepDate") String keepDate);

    void insertMqCountResult(MqCountResult mqCountResult);

    /**
     * 统计日期范围内的数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    List<MqCountResult> getSenderCountList(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<MqCountResult> getReceiverCountList(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
