package com.jraska.gpsbatterytest.logging;

import com.jraska.common.ArgumentCheck;

import java.io.*;

public class TextFileLogger implements ILogger
{
	//region Fields

	private final File mFile;
	private boolean mFileCreated = false;
	private BufferedWriter mWriter;

	private boolean mDisposed;

	//endregion

	//region Constructors

	public TextFileLogger(File textFile)
	{
		ArgumentCheck.notNull(textFile);

		if (!textFile.getParentFile().exists())
		{
			throw new IllegalArgumentException("Parent folder of file " + textFile + " does not exist.");
		}

		mFile = textFile;

		if (mFile.exists())
		{
			mFileCreated = true;
		}
	}

	//endregion

	//region Properties

	@Override
	public void log(Object o)
	{
		ArgumentCheck.notNull(o);

		ensureNotDisposed();

		try
		{
			ensureCreatedAndOpened();
			mWriter.write(o.toString());
			mWriter.newLine();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dispose()
	{
		if (mDisposed)
		{
			return;
		}

		try
		{
			if (mWriter != null)
			{
				mWriter.close();
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			mDisposed = true;
		}
	}

	//endregion

	//region Methods

	protected final void ensureNotDisposed()
	{
		if (mDisposed)
		{
			throw new IllegalStateException(getClass().getSimpleName() + " is disposed!");
		}
	}

	protected final void ensureCreatedAndOpened() throws IOException
	{
		if (!mFileCreated)
		{
			mFileCreated = mFile.createNewFile();
		}

		if (mWriter == null)
		{
			FileOutputStream outputStream = new FileOutputStream(mFile);
			mWriter = new BufferedWriter(new OutputStreamWriter(outputStream), 256);
		}
	}

	//endregion
}
