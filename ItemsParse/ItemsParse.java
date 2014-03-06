import java.io.*;
import java.util.*;

class ItemsParse {

	private static final String FILE_NAME = "items.txt";
	private static final String OUT_FILE_NAME = "items.json";

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("\nItemsParse\n");

		File file = new File(FILE_NAME);

		if (file == null) {
			System.out.println("Could not find file " + FILE_NAME);
			return;
		}

		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		ParseObject parseObject = ItemsParse.readObject(bufferedReader);

		List<ParseObject> items = new LinkedList<ParseObject>();

		if (parseObject instanceof ParseList) {
			ParseList list = (ParseList) parseObject;

			for (ParseObject obj : list.value) {
				if (obj.isItem()) {
					items.add(obj);
				}
			}
		}

		boolean writeALLJson = false;
		String json = null;

		if (writeALLJson) {
			System.out.println("Writing all parsed data to JSON...");
			json = "{" + parseObject.getJsonString() + "}";
		}
		else {
			System.out.println("Writing " + items.size() + " items to JSON...");
			json = "{\"result\":{\"items\":[";

			for (ParseObject item : items) {
				json += "{";

				json += "\"name\":\"" + item.key + "\"";

				if (item instanceof ParseList) {
					ParseList list = (ParseList) item;
					for (ParseObject obj : list.value) {
						if (obj.key.equals("ID")) {
							ParseString idObj = (ParseString)obj;
							json += ",\"id\":\"" + idObj.value + "\"";
						}
					}
				}

				json += "}";

				if (items.indexOf(item) != items.size() - 1) {
					json += ",";
				}
			}

			json += "]}}";
		}

		PrintWriter writer = null;

		try {
			writer = new PrintWriter(OUT_FILE_NAME, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {

		}

		if (writer != null) {
			writer.println(json);
			writer.close();
			System.out.println("Success!\n");
		}
		else {
			System.out.println("Error writing to json file.");
		}
	}

	private static ParseObject readObject(BufferedReader reader) {
		ParseObject obj = null;

		String firstLine = readNextLine(reader);

		int nameStartQuoteIndex = firstLine.indexOf("\"");
		int nameEndQuoteIndex = firstLine.indexOf("\"", nameStartQuoteIndex + 1);

		if (nameStartQuoteIndex < 0) {
			return null;
		}

		String key = firstLine.substring(nameStartQuoteIndex + 1, nameEndQuoteIndex);

		firstLine = firstLine.substring(nameEndQuoteIndex + 1);

		int stringValueStartQuoteIndex = firstLine.indexOf("\"");

		if (stringValueStartQuoteIndex >= 0) {
			//its a ParseString object
			int stringValueEndQuoteIndex = firstLine.indexOf("\"", stringValueStartQuoteIndex + 1);
			String stringValue = firstLine.substring(stringValueStartQuoteIndex + 1, stringValueEndQuoteIndex);

			obj = new ParseString(key, stringValue);
		}
		else {
			obj = readList(key, reader);
		}

		return obj;
	}

	private static ParseList readList(String key, BufferedReader reader) {
		String firstLine = readNextLine(reader).trim();

		if (!firstLine.equals("{")) {
			return null;
		}

		List<ParseObject> list = new LinkedList<>();
		ParseObject newObject = null;

		while ((newObject = readObject(reader)) != null) {
			list.add(newObject);
		}

		return new ParseList(key, list);
	}

	private static String readNextLine(BufferedReader reader) {
		String line = null;

		try {
			line = reader.readLine(); 
		}
		catch (IOException e) {
			System.out.println("Error reading line: " + e.toString());
			return null;
		}
		 
		if (line != null)
		{
			int commentIndex = line.indexOf("//");

			if (commentIndex > 0) {
				line = line.substring(0, commentIndex);
			}
			else if (commentIndex == 0) {
				line = "";
			}

			if (line.trim().length() <= 0) {
				line = null;
			}
		}

		if (line == null) {
			return readNextLine(reader);
		}
		else {
			return line;
		}
	}

	private static abstract class ParseObject {
		String key;

		boolean isItem() {
			return key.startsWith("item_");
		}

		abstract String getDescription();
		abstract String getJsonString();
	}

	private static class ParseString extends ParseObject {
		String value;

		public ParseString(String key, String value) {
			this.key = key;
			this.value = value;
		}

		String getDescription() {
			return key + " : " + value;
		}

		String getJsonString() {
			return "\"" + key + "\":\"" + value + "\"";
		}
	}

	private static class ParseList extends ParseObject {
		List<ParseObject> value;

		public ParseList(String key, List<ParseObject> list) {
			this.key = key;
			this.value = list;
		}

		String getDescription() {
			return key + " : " + value.size() + " elements";
		}

		String getJsonString() {
			String json = "\"" + key + "\":";

			json += "{";

			for (ParseObject obj : value) {
				json += obj.getJsonString();

				if (value.indexOf(obj) != value.size() - 1) {
					json += ",";
				}
			}

			json += "}";

			return json;
		}
	}
}
