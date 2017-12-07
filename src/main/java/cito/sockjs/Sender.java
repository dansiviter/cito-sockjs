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

import java.io.IOException;
import java.util.Queue;

/**
 * @author Daniel Siviter
 * @since v1.0 [14 Feb 2017]
 */
public interface Sender extends AutoCloseable {
	/**
	 * 
	 * @param frame the frame to send.
	 * @throws IOException
	 */
	void send(Queue<String> frames) throws IOException;

	/**
	 * Overridden to limit exception.
	 */
	@Override
	void close() throws IOException;
}
