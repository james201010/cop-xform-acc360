/**
 * 
 */
package com.rudetools.otel.xform;

/**
 * This is a marker interface for now.
 * 
 * The strings in the string array returned from the xform method should adhere to the OTLP Json Protobuf format 
 * seen in these examples (metrics, traces, logs) in the link below.
 * 
 * https://github.com/open-telemetry/opentelemetry-proto/tree/main/examples
 * 
 * @author james101
 *
 */
public interface XForm2OtlpJsonProto extends XForm {

}
