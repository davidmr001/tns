package com.qibaike.thriftnameserver.command.push;

import java.util.List;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.qibaike.thriftnameserver.rpc.TCNode;
import com.qibaike.thriftnameserver.rpc.TSNode;

public abstract class BaseThriftPushCommand<T> extends HystrixCommand<T> {
	protected final TCNode tcnode;
	protected final List<TCNode> cList;
	protected final List<TSNode> sList;

	protected BaseThriftPushCommand(TCNode tcnode, List<TCNode> cList, List<TSNode> sList) {
		super(setter);
		this.tcnode = tcnode;
		this.cList = cList;
		this.sList = sList;
	}

	private static final HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory
			.asKey("PushGroup");
	private static final HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("node");
	private static final HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory
			.asKey("T-ThriftPushGroup");

	private static final HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties
			.Setter().withExecutionTimeoutInMilliseconds(5000).withFallbackEnabled(true);
	private static final HystrixThreadPoolProperties.Setter threadPoolProperties = HystrixThreadPoolProperties
			.Setter().withCoreSize(1).withMaxQueueSize(2);
	public static final HystrixCommand.Setter setter = HystrixCommand.Setter.withGroupKey(groupKey)
			.andCommandKey(commandKey).andThreadPoolKey(threadPoolKey)
			.andCommandPropertiesDefaults(commandProperties)
			.andThreadPoolPropertiesDefaults(threadPoolProperties);

	public T push() {
		this.logPush(tcnode);
		return this.execute();
	}

	protected abstract void logPush(TCNode tcnode);
}
