/*
 * Copyright 2016-2017 Daniel Siviter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cito.sockjs;

import static cito.sockjs.InfoHandler.INFO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for {@link InfoHandler}.
 * 
 * @author Daniel Siviter
 * @since v1.0 [29 Dec 2016]
 * @see <a href="https://sockjs.github.io/sockjs-protocol/sockjs-protocol-0.3.3.html#section-26">SockJS 0.3.3 Info</a>
 */
public class InfoIT extends AbstractIT {
	/**
	 * This url is called before the client starts the session. It's used to check server capabilities (websocket
	 * support, cookies requirement) and to get the value of "origin" setting (currently not used).
	 * But more importantly, the call to this url is used to measure the roundtrip time between the client and the
	 * server. So, please, do respond to this url in a timely fashion.
	 */
	@Test
	@RunAsClient
	public void test_basic() {
		try (ClosableResponse res = get(target().path(INFO))) {
			assertEquals(Status.OK, res.getStatusInfo());
			assertEquals("application/json;charset=UTF-8", res.getHeaderString(HttpHeaders.CONTENT_TYPE));
			verifyNoCookie(res);
			verifyNotCached(res);
			verifyCors(res, null);

			final JsonObject json = res.readEntity(JsonObject.class);
			assertTrue(json.getBoolean("websocket"));
			assertNotNull(json.getBoolean("cookie_needed"));
			final JsonArray origins = json.getJsonArray("origins");
			assertEquals(1, origins.size());
			assertEquals("*:*", origins.getString(0));
			assertNotNull(json.getJsonNumber("entropy"));
		}
	}

	/**
	 * As browsers don't have a good entropy source, the server must help with tht. Info url must supply a good, unpredictable random number from the range <0; 2^32-1> to feed the browser.
	 */
	@Test
	@RunAsClient
	public void test_entropy() {
		final long entropy0;
		try (ClosableResponse res = get(target().path(INFO).request(MediaType.APPLICATION_JSON_TYPE))) {
			final JsonObject json = res.readEntity(JsonObject.class);
			entropy0 = json.getJsonNumber("entropy").longValue();
		}
		final long entropy1;
		try (ClosableResponse res = get(target().path(INFO))) {
			final JsonObject json = res.readEntity(JsonObject.class);
			entropy1 = json.getJsonNumber("entropy").longValue();
		}
		assertNotEquals(entropy0, entropy1);
	}

	/**
	 * Info url must support CORS.
	 */
	@Test
	@RunAsClient
	public void test_options() {
		verifyOptions("info", "GET", "OPTIONS");
	}

	/**
	 * The 'disabled_websocket_echo' service should have websockets disabled.
	 */
	@Test
	@RunAsClient
	@Ignore
	public void test_disabled_websocket() {
		//    def test_disabled_websocket(self):
		//        r = GET(wsoff_base_url + '/info')
		//        self.assertEqual(r.status, 200)
		//        data = json.loads(r.body)
		//        self.assertEqual(data['websocket'], False)
	}


	// --- Static Methods ---

	@Deployment
	public static WebArchive createDeployment() {
		return createWebArchive();
	}
}
