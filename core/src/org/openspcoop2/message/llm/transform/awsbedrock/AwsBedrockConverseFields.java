/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.message.llm.transform.awsbedrock;

/**
 * Costanti dei nomi di campo del JSON di AWS Bedrock Converse API,
 * usati sia dal trasformatore outbound (canonical → Bedrock) sia
 * dall'inbound (Bedrock → canonical).
 * <p>
 * Riferimento API:
 * <a href="https://docs.aws.amazon.com/bedrock/latest/APIReference/API_runtime_Converse.html">
 * Amazon Bedrock Runtime — Converse</a>.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class AwsBedrockConverseFields {

	/** Path della Converse API: {@code /model/{modelId}/converse} (sync) o {@code /converse-stream}. */
	public static final String RESOURCE_PATH_PREFIX = "/model/";
	public static final String RESOURCE_PATH_SUFFIX_SYNC = "/converse";
	public static final String RESOURCE_PATH_SUFFIX_STREAM = "/converse-stream";

	/* Request top-level */
	public static final String FIELD_MESSAGES = "messages";
	public static final String FIELD_SYSTEM = "system";
	public static final String FIELD_INFERENCE_CONFIG = "inferenceConfig";
	public static final String FIELD_TOOL_CONFIG = "toolConfig";

	/* Message */
	public static final String FIELD_ROLE = "role";
	public static final String FIELD_CONTENT = "content";

	/* Content blocks */
	public static final String BLOCK_TEXT = "text";
	public static final String BLOCK_TOOL_USE = "toolUse";
	public static final String BLOCK_TOOL_RESULT = "toolResult";
	public static final String FIELD_TOOL_USE_ID = "toolUseId";
	public static final String FIELD_TOOL_USE_NAME = "name";
	public static final String FIELD_TOOL_USE_INPUT = "input";

	/* InferenceConfig */
	public static final String FIELD_MAX_TOKENS = "maxTokens";
	public static final String FIELD_TEMPERATURE = "temperature";
	public static final String FIELD_TOP_P = "topP";
	public static final String FIELD_STOP_SEQUENCES = "stopSequences";

	/* Response */
	public static final String FIELD_OUTPUT = "output";
	public static final String FIELD_MESSAGE = "message";
	public static final String FIELD_STOP_REASON = "stopReason";
	public static final String FIELD_USAGE = "usage";
	public static final String FIELD_INPUT_TOKENS = "inputTokens";
	public static final String FIELD_OUTPUT_TOKENS = "outputTokens";
	public static final String FIELD_TOTAL_TOKENS = "totalTokens";

	private AwsBedrockConverseFields() {
		// utility class
	}
}
