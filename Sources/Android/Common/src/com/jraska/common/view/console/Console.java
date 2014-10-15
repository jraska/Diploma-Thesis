package com.jraska.common.view.console;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

public class Console extends ScrollView
{
	//region Static Fields

	private static ArrayList<WeakReference<Console>> sConsoles = new ArrayList<WeakReference<Console>>();
	private static Handler sHandler = new ConsoleHandler(Looper.getMainLooper()); //main looper ensures UI thread

	private static String sConsoleText = "";

	//endregion

	//region Constants

	private static final int MAX_TEXT_LENGTH = 5000;

	private static final int WRITE_MESSAGE_ID = 1;
	private static final int CLEAR_CONSOLE_ID = 2;

	//endregion

	//region Fields

	private final TextView mTextView;

	//endregion

	//region Constructors

	public Console(Context context)
	{
		this(context, null);
	}

	public Console(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public Console(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		mTextView = new TextView(context);
		addView(mTextView);

		sConsoles.add(new WeakReference<Console>(this));
		updateText(sConsoleText);
	}

	//endregion

	//region Methods

	private void updateText(String fullText)
	{
		mTextView.setText(fullText);
		fullScroll(ScrollView.FOCUS_DOWN);
	}

	//endregion

	//region Static Methods

	public static void writeLn(Object o)
	{
		if (o == null)
		{
			writeLn("null");
		}
		else
		{
			writeLn(o.toString());
		}
	}

	public static void writeLn(String text)
	{
		write(text + "\n");
	}

	public static void write(String text)
	{
		sendMessage(WRITE_MESSAGE_ID, text);
	}

	public static void clear()
	{
		sendMessage(CLEAR_CONSOLE_ID, null);
	}

	private static void sendMessage(int id, Object obj)
	{
		Message.obtain(sHandler, id, obj).sendToTarget();
	}

	private static void writeInternal(String text)
	{
		String textBefore = sConsoleText;
		if (textBefore.length() > MAX_TEXT_LENGTH)
		{
			textBefore = textBefore.substring(textBefore.length() - MAX_TEXT_LENGTH + 1);
		}

		sConsoleText = textBefore + text;

		setConsolesText(sConsoleText);
	}

	private static void setConsolesText(String text)
	{
		//going backwards to allow remove dropped consoles
		for (int i = sConsoles.size() - 1; i >= 0; i--)
		{
			WeakReference<Console> consoleRef = sConsoles.get(i);
			Console console = consoleRef.get();
			if (console == null)
			{
				sConsoles.remove(i);
				continue;
			}

			console.updateText(text);
		}
	}

	private static void clearInternal()
	{
		sConsoleText = "";
		setConsolesText(sConsoleText);
	}

	//endregion

	//region Nested classes

	static class ConsoleHandler extends Handler
	{
		ConsoleHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case WRITE_MESSAGE_ID:
					writeInternal((String) msg.obj);
					break;

				case CLEAR_CONSOLE_ID:
					clearInternal();
					break;

				default:
					super.handleMessage(msg);
			}
		}
	}

	//endregion
}
