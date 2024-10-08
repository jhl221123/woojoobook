package com.e207.woojoobook.global.config.db;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

	private CircularList<String> dataSourceNameList;

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);

		dataSourceNameList = new CircularList<>(
			targetDataSources.keySet()
				.stream()
				.map(Object::toString)
				.filter(string -> string.contains("slave"))
				.collect(Collectors.toList())
		);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		if (isReadOnly) {
			return dataSourceNameList.getOne();
		} else {
			return "master";
		}
	}
}
