package aisaac.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;

import aisaac.model.DruidRequest;
import in.zapr.druid.druidry.filter.AndFilter;
import in.zapr.druid.druidry.filter.BoundFilter;
import in.zapr.druid.druidry.filter.DruidFilter;
import in.zapr.druid.druidry.filter.NotFilter;
import in.zapr.druid.druidry.filter.OrFilter;
import in.zapr.druid.druidry.filter.SelectorFilter;
import in.zapr.druid.druidry.query.config.Interval;
import in.zapr.druid.druidry.query.config.SortingOrder;

public class QueryUtils {
	
	public static Interval extractInterval(DruidRequest request) {
	
		DateTime startTime = DateTime.parse(request.getIntervalStart());
		
		DateTime endTime = DateTime.parse(request.getIntervalEnd());
		
		if (startTime.isEqual((ReadableInstant) endTime))
			endTime = endTime.plus((ReadablePeriod) Period.days(1));
		
		return new Interval(startTime, endTime);
	}

	public static Optional<DruidFilter> buildFilter(DruidRequest request) {
		List<DruidFilter> andFilters = new LinkedList<>();
		List<DruidFilter> orFilters = new LinkedList<>();
		
		String query = request.getQueryString().trim();
		if(StringUtils.containsIgnoreCase(query, " and ")) {
			andFilters = processFilters(query, "and");
		}
		
		if(StringUtils.containsIgnoreCase(query, " or ")) {
			orFilters = processFilters(query, "or");
		}
		
		if (query.isEmpty())
			return Optional.empty();
		
		if(andFilters.isEmpty() && orFilters.isEmpty())
			return Optional.of(addCondition(query));
		
		if(orFilters.isEmpty())
			return (Optional) Optional.of(new AndFilter(andFilters));
		
		if(andFilters.isEmpty())
			return (Optional) Optional.of(new OrFilter(orFilters));
		
		return Optional.empty();  // contains both AND & OR conditions. TODO.
	}
	
	public static List<DruidFilter> processFilters(String query, String andOr) {
		List<DruidFilter> filters = new LinkedList<>();
		boolean exclude = false;
		
		StringBuffer s = new StringBuffer(query).append(" ").append(andOr);
		StringBuffer str = new StringBuffer();
		
		int andOrLength = andOr.length();

		for (int i = 0; i < s.length() - andOrLength; i++) {
			if (s.charAt(i) == '\'' && !(s.charAt(i - 1) == '\\')) {
				exclude = !exclude;
			}

			if ((!s.substring(i, i + andOrLength).equalsIgnoreCase(andOr) || exclude) && (i != s.length() - (andOrLength + 1))) {
				str.append(s.charAt(i));
			} else {
				System.out.println(str);
				filters.add(addCondition(str.toString()));
				str = new StringBuffer();
				i += andOrLength;
			}
		}
		
		return filters;
	}

	private static DruidFilter addCondition(String condition) {
		
		if(condition.contains("!=")) {
			return new NotFilter(
					new SelectorFilter(condition.split("!=")[0].trim(), condition.split("!=")[1].trim().replaceAll("'", "")));
			
		} else if(StringUtils.containsIgnoreCase(condition, " is not ")) {
			//We cant split by ignore case. Hence converting "IS NOT" to lowercase and then we need to split.
			String conditionLower = condition.replaceAll("(?i)" + Pattern.quote(" is not "), " is not ");
			String[] columnAndValue = conditionLower.split(" is not ");
			String value = "null".equalsIgnoreCase(columnAndValue[1].trim().replaceAll("'", "")) ? null : columnAndValue[1].trim().replaceAll("'", "");
			return new NotFilter(
					new SelectorFilter(columnAndValue[0].trim(), value));
			
		} else if(condition.contains("<=")) {
			return BoundFilter.builder()
					.dimension(condition.split("<=")[0].trim())
					.upper(condition.split("<=")[1].trim().replaceAll("'", ""))
					.upperStrict(false)
					.lowerStrict(false)
					.ordering(SortingOrder.NUMERIC).build();
			
		}else if(condition.contains("<")) {
			return BoundFilter.builder()
					.dimension(condition.split("<")[0].trim())
					.upper(condition.split("<")[1].trim().replaceAll("'", ""))
					.upperStrict(true)
					.lowerStrict(false)
					.ordering(SortingOrder.NUMERIC).build();
			
		}else if(condition.contains(">=")) {
			return BoundFilter.builder()
					.dimension(condition.split(">=")[0].trim())
					.lower(condition.split(">=")[1].trim().replaceAll("'", ""))
					.upperStrict(false)
					.lowerStrict(false)
					.ordering(SortingOrder.NUMERIC).build();
			
		}else if(condition.contains(">")) {
			return BoundFilter.builder()
					.dimension(condition.split(">")[0].trim())
					.lower(condition.split(">")[1].trim().replaceAll("'", ""))
					.lowerStrict(true)
					.upperStrict(false)
					.ordering(SortingOrder.NUMERIC).build();
			
		}else if(condition.contains("=")) {
			return new SelectorFilter(condition.split("=")[0].trim(), condition.split("=")[1].trim().replaceAll("'", "")); 
		
		}else if(StringUtils.containsIgnoreCase(condition, " is ")) {
			//We cant split by ignore case. Hence converting "IS" to lowercase and then we need to split.
			String conditionLower = condition.replaceAll("(?i)" + Pattern.quote(" is "), " is ");
			String[] columnAndValue = conditionLower.split(" is ");
			String value = "null".equalsIgnoreCase(columnAndValue[1].trim().replaceAll("'", "")) ? null : columnAndValue[1].trim().replaceAll("'", "");
			return new SelectorFilter(columnAndValue[0].trim(), value);
		
		}

		return null;
	}

}
