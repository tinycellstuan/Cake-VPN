
package org.strongswan.android.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;



public class SettingsWriter
{

	private final SettingsSection mTop = new SettingsSection();


	public SettingsWriter setValue(String key, String value)
	{
		Pattern pattern = Pattern.compile("[^#{}=\"\\n\\t ]+");
		if (key == null || !pattern.matcher(key).matches())
		{
			return this;
		}
		String[] keys = key.split("\\.");
		SettingsSection section = mTop;
		section = findOrCreateSection(Arrays.copyOfRange(keys, 0, keys.length-1));
		section.Settings.put(keys[keys.length-1], value);
		return this;
	}


	public SettingsWriter setValue(String key, Integer value)
	{
		return setValue(key, value == null ? null : value.toString());
	}

	public SettingsWriter setValue(String key, Boolean value)
	{
		return setValue(key, value == null ? null : value ? "1" : "0");
	}


	public String serialize()
	{
		StringBuilder builder = new StringBuilder();
		serializeSection(mTop, builder);
		return builder.toString();
	}


	private void serializeSection(SettingsSection section, StringBuilder builder)
	{
		for (Entry<String, String> setting : section.Settings.entrySet())
		{
			builder.append(setting.getKey()).append('=');
			if (setting.getValue() != null)
			{
				builder.append("\"").append(escapeValue(setting.getValue())).append("\"");
			}
			builder.append('\n');
		}

		for (Entry<String, SettingsSection> subsection : section.Sections.entrySet())
		{
			builder.append(subsection.getKey()).append(" {\n");
			serializeSection(subsection.getValue(), builder);
			builder.append("}\n");
		}
	}


	private String escapeValue(String value)
	{
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}


	private SettingsSection findOrCreateSection(String[] sections)
	{
		SettingsSection section = mTop;
		for (String name : sections)
		{
			SettingsSection subsection = section.Sections.get(name);
			if (subsection == null)
			{
				subsection = new SettingsSection();
				section.Sections.put(name, subsection);
			}
			section = subsection;
		}
		return section;
	}


	private class SettingsSection
	{

		LinkedHashMap<String,String> Settings = new LinkedHashMap<String, String>();


		LinkedHashMap<String,SettingsSection> Sections = new LinkedHashMap<String, SettingsWriter.SettingsSection>();
	}
}
