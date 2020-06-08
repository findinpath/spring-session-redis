package com.findinpath.config;

import io.lettuce.core.ReadFrom;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {

	private RedisInstance master;
	private String password;
	private List<RedisInstance> replicas;

	RedisInstance getMaster() {
		return master;
	}

	void setMaster(RedisInstance master) {
		this.master = master;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	List<RedisInstance> getReplicas() {
		return replicas;
	}

	void setSlaves(List<RedisInstance> replicas) {
		this.replicas = replicas;
	}



	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
				.readFrom(ReadFrom.REPLICA_PREFERRED)
				.build();
		RedisStaticMasterReplicaConfiguration staticMasterReplicaConfiguration = new RedisStaticMasterReplicaConfiguration(this.getMaster().getHost(), this.getMaster().getPort());
		staticMasterReplicaConfiguration.setPassword(RedisPassword.of(password));
		this.getReplicas().forEach(replica -> staticMasterReplicaConfiguration.addNode(replica.getHost(), replica.getPort()));
		return new LettuceConnectionFactory(staticMasterReplicaConfiguration, clientConfig);
	}

	private static class RedisInstance {

		private String host;
		private int port;

		String getHost() {
			return host;
		}

		void setHost(String host) {
			this.host = host;
		}

		int getPort() {
			return port;
		}

		void setPort(int port) {
			this.port = port;
		}
	}

}
