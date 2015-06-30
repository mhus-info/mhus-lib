package de.mhus.lib.jms.util;

import java.util.Enumeration;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

public class MessageStringifier {

	private Message msg;

	public MessageStringifier(Message msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		if (msg == null) return "null";
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("=== JMS Message === ").append(msg.getClass().getSimpleName()).append(" ===\n");
			sb.append("Message ID    : ").append(msg.getJMSMessageID()).append('\n');
			//sb.append("Destination   : ").append(msg.getJMSDestination()).append('\n');
			//sb.append("Type          : ").append(msg.getJMSType()).append('\n');
			sb.append("Reply         : ").append(msg.getJMSReplyTo()).append('\n');
			sb.append("Correlation ID: ").append(msg.getJMSCorrelationID()).append('\n');
			
			for (@SuppressWarnings("unchecked")Enumeration<String> e = msg.getPropertyNames();e.hasMoreElements();) {
				String key = e.nextElement();
				sb.append("  ").append(key).append('=').append(msg.getStringProperty(key)).append('\n');
			}
			
			if (msg instanceof MapMessage) {
				sb.append("Map:\n");
				MapMessage m = (MapMessage)msg;
				for (@SuppressWarnings("unchecked")Enumeration<String> e = m.getMapNames();e.hasMoreElements();) {
					String key = e.nextElement();
					sb.append("  ").append(key).append('=').append(((MapMessage) msg).getString(key)).append('\n');
				}
			} else
			if (msg instanceof TextMessage) {
				sb.append("Text: ").append( ((TextMessage)msg).getText()).append('\n');
			} else
			if (msg instanceof BytesMessage) {
				sb.append("Size: " + ((BytesMessage)msg).getBodyLength() );
			}
		} catch (Throwable t) {
			sb.append(t);
		}
		return sb.toString();
	}
}
