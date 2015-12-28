package cn.vstore.appserver.util;


import com.meetup.memcached.SockIOPool;

public class MemberCacheUtil {
	protected static SockIOPool pool = null;
	// set up connection pool once at class load
	public MemberCacheUtil(){
		// Server list
//		String[] servers = { "10.8.10.79:12000" };
//		String[] servers = { "10.8.10.12:12000" };
//		String[] servers = { "10.8.10.79:12000" };
		String[] servers = { "10.8.10.12:12000" };

		// Specify memcached capacity
		Integer[] weights = { 3, 3, 2 };

		/*
		 * String[] serverlist = { "cache0.server.com:12345",
		 * "cache1.server.com:12345" }; Integer[] weights = { new Integer(5),
		 * new Integer(2) }; int initialConnections = 10; int
		 * minSpareConnections = 5; int maxSpareConnections = 50; long
		 * maxIdleTime = 1000 * 60 * 30; // 30 minutes long maxBusyTime = 1000 *
		 * 60 * 5; // 5 minutes long maintThreadSleep = 1000 * 5; // 5 seconds
		 * int socketTimeOut = 1000 * 3; // 3 seconds to block on reads int
		 * socketConnectTO = 1000 * 3; // 3 seconds to block on initial
		 * connections. If 0, then will use blocking connect (default) boolean
		 * failover = false; // turn off auto-failover in event of server down
		 * boolean nagleAlg = false; // turn off Nagle's algorithm on all
		 * sockets in pool boolean aliveCheck = false; // disable health check
		 * of socket on checkout
		 * 
		 * SockIOPool pool = SockIOPool.getInstance();
		 * pool.setServers(serverlist); pool.setWeights(weights);
		 * pool.setInitConn(initialConnections);
		 * pool.setMinConn(minSpareConnections);
		 * pool.setMaxConn(maxSpareConnections); pool.setMaxIdle(maxIdleTime);
		 * pool.setMaxBusyTime(maxBusyTime);
		 * pool.setMaintSleep(maintThreadSleep);
		 * pool.setSocketTO(socketTimeOut); pool.setNagle(nagleAlg);
		 * pool.setHashingAlg(SockIOPool.NEW_COMPAT_HASH);
		 * pool.setAliveCheck(true); pool.initialize();
		 */

		// grab an instance of our connection pool
		pool = SockIOPool.getInstance();

		// set the servers and the weights
		pool.setServers(servers);
		pool.setWeights(weights);

		// Specify main thread maintain frequency
		pool.setMaintSleep(30);

		// set some TCP settings
		// disable nagle
		pool.setNagle(false);
		// set the read timeout to 3 secs
		pool.setSocketTO(3000);
		// and don't set a connect timeout
		pool.setSocketConnectTO(0);

		// initialize the connection pool
		pool.initialize();
		
	}
}
