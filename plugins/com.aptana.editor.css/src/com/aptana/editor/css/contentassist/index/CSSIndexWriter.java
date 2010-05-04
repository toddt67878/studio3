package com.aptana.editor.css.contentassist.index;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.UserAgentElement;
import com.aptana.editor.css.contentassist.model.ValueElement;
import com.aptana.index.core.Index;
import com.aptana.util.StringUtil;

public class CSSIndexWriter
{
	private CSSMetadataReader _reader;
	private Map<UserAgentElement,String> _userAgentKeyMap = new HashMap<UserAgentElement,String>();
	private int _valueCount;
	
	/**
	 * CSSIndexWriter
	 */
	public CSSIndexWriter()
	{
		this._reader = new CSSMetadataReader();
	}
	
	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected String getDocumentPath()
	{
		return CSSIndexConstants.METADATA;
	}
	
	/**
	 * loadXML
	 * 
	 * @param stream
	 * @throws Exception 
	 * @throws ScriptDocException
	 */
	public void loadXML(InputStream stream) throws Exception
	{
		this._reader.loadXML(stream);
	}

	/**
	 * writeElement
	 * 
	 * @param index
	 * @param element
	 */
	protected void writeElement(Index index, ElementElement element)
	{
		String[] columns = new String[] {
			element.getName(),
			element.getDisplayName(),
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, element.getUserAgents())),
			element.getDescription(),
			element.getExample(),
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, element.getProperties()),
			element.getRemark()
		};
		String key = StringUtil.join(CSSIndexConstants.DELIMITER, columns);
		
		index.addEntry(CSSIndexConstants.ELEMENT, key, this.getDocumentPath());
	}
	
	/**
	 * writeProperty
	 * 
	 * @param index
	 * @param property
	 */
	protected void writeProperty(Index index, PropertyElement property)
	{
		String[] columns = new String[] {
			property.getName(),
			Boolean.toString(property.allowMultipleValues()),
			property.getType(),
			// specifications
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, property.getUserAgents())),
			property.getDescription(),
			property.getExample(),
			property.getHint(),
			property.getRemark(),
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, this.writeValues(index, property.getValues()))
		};
		String key = StringUtil.join(CSSIndexConstants.DELIMITER, columns);
		
		index.addEntry(CSSIndexConstants.PROPERTY, key, this.getDocumentPath());
	}

	/**
	 * writeToIndex
	 * 
	 * @param index
	 */
	public void writeToIndex(Index index)
	{
		for (ElementElement element : this._reader.getElements())
		{
			this.writeElement(index, element);
		}
		
		for (PropertyElement property : this._reader.getProperties())
		{
			this.writeProperty(index, property);
		}
	}
	
	/**
	 * writeUserAgent
	 * 
	 * @param index
	 * @param userAgent
	 * @return
	 */
	protected String writeUserAgent(Index index, UserAgentElement userAgent)
	{
		String key = this._userAgentKeyMap.get(userAgent);
		
		if (key == null)
		{
			key = Integer.toString(this._userAgentKeyMap.size());
			
			String[] columns = new String[] {
				key,
				userAgent.getDescription(),
				userAgent.getOS(),
				userAgent.getPlatform(),
				userAgent.getVersion()
			};
			String value = StringUtil.join(CSSIndexConstants.DELIMITER, columns);
			
			index.addEntry(CSSIndexConstants.USER_AGENT, value, this.getDocumentPath());
			
			this._userAgentKeyMap.put(userAgent, key);
		}
		
		return key;
	}
	
	/**
	 * writeUserAgents
	 * 
	 * @param userAgents
	 * @return
	 */
	protected List<String> writeUserAgents(Index index, List<UserAgentElement> userAgents)
	{
		List<String> keys = new LinkedList<String>();
		
		for (UserAgentElement userAgent : userAgents)
		{
			keys.add(this.writeUserAgent(index, userAgent));
		}
		
		return keys;
	}
	
	/**
	 * writeValue
	 * 
	 * @param index
	 * @param value
	 * @return
	 */
	protected String writeValue(Index index, ValueElement value)
	{
		String key = Integer.toString(this._valueCount++);
		
		String[] columns = new String[] {
			key,
			value.getName(),
			value.getDescription(),
			StringUtil.join(CSSIndexConstants.SUB_DELIMITER, this.writeUserAgents(index, value.getUserAgents()))
		};
		String valueString = StringUtil.join(CSSIndexConstants.DELIMITER, columns);
		
		index.addEntry(CSSIndexConstants.VALUE, valueString, this.getDocumentPath());
		
		return key;
	}
	
	/**
	 * writeValues
	 * 
	 * @param index
	 * @param values
	 * @return
	 */
	protected List<String> writeValues(Index index, List<ValueElement> values)
	{
		List<String> keys = new LinkedList<String>();
		
		for (ValueElement value : values)
		{
			keys.add(this.writeValue(index, value));
		}
		
		return keys;
	}
}
