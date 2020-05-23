package tech.xuanwu.northstar.core.persistence.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.xuanwu.northstar.entity.CtpSettingInfo;
import tech.xuanwu.northstar.entity.CtpSettingInfo.ConnectionType;
import tech.xuanwu.northstar.entity.CtpSettingInfo.MarketType;

@Repository
public interface CtpSettingRepo extends MongoRepository<CtpSettingInfo, String>{

	List<CtpSettingInfo> findByConnectionTypeAndMarketType(ConnectionType connectionType, MarketType marketType);
}
