package app.executor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

import javax.swing.JTextArea;

import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.http.HttpOption;
import app.util.JsonUtil;

public class RedirectProcessExecutor {
	private ObjectMapper objectMapper = new ObjectMapper();

	public void run(String[] args, JTextArea outputTextArea) {

		try {

			System.out.println("RedirectProcessExecutor-Parameters");
			for (int i = 0; i < args.length; i++) {
				System.out.println("arg" + i + " : " + args[i]);
			}

			ProcessExecutor processExecutor = new ProcessExecutor();

			processExecutor.command(args).redirectOutput(new LogOutputStream() {

				boolean isFirst = true;
				boolean isResult = false;

				@Override
				protected void processLine(String arg0) {

					try {
						byte[] decodedBytes = Base64.getDecoder().decode(
								arg0.getBytes("UTF-8"));
						String decodedStr = new String(decodedBytes, "UTF-8");

						if (isFirst) {
							outputTextArea.setText(null);
							isFirst = false;
						}

						if (isResult) {
							printResultString(decodedStr, outputTextArea);
						} else {
							
							if (decodedStr.indexOf("* Response Body :") > -1) {
								isResult = true;
							}
							
							outputTextArea.append(decodedStr);
							outputTextArea.append("\n");
						}
						
					} catch (UnsupportedEncodingException e) {
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						
						outputTextArea.setText(null);
						outputTextArea.append("***ERROR***\n");
						outputTextArea.append(errors.toString());
					}
				}
			}).execute();

		} catch (InvalidExitValueException | IOException | InterruptedException
				| TimeoutException e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			
			outputTextArea.setText(null);
			outputTextArea.append("***ERROR***\n");
			outputTextArea.append(errors.toString());
		}
	}

	private void printResultString(String resultString, JTextArea outputTextArea) {
		if (HttpOption.isJSONPretty()) {
			String prettyJson = JsonUtil.getPrettyJson(resultString);
			outputTextArea.append(prettyJson);
		} else {
			outputTextArea.append(resultString);
		}
		
		outputTextArea.select(0, 0);
	}
}
