/**
 * 
 */
package aisaac.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jaskiran
 *
 */
@Getter
@Setter
public class LMDeepBiQueryContext {
	Boolean skipEmptyBuckets;
	Boolean bySegment;
}
