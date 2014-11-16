/*
 * Copyright 2014 rainer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.rasptinker.model;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import static de.jdynameta.rasptinker.model.ScpFrom.checkAck;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

/**
 *
 * @author rainer
 */
public class TakePhotoFromRasp
{

	public static void main(String[] arg)
	{
//    if(arg.length!=2){
//      System.err.println("usage: java ScpFrom user@remotehost:file1 file2");
//      System.exit(-1);
//    }      

		FileOutputStream fos = null;
		try
		{

			String user = "pi";
			String host = "192.168.178.71";
			String lfile = "/home/rainer";

			String prefix = null;
			if (new File(lfile).isDirectory())
			{
				prefix = lfile + File.separator;
			}

			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 22);

			// username and password will be given via UserInfo interface.
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();

			// exec take photo with raspistill
			String pFileName = "/home/pi/pic_" + Instant.now() + ".jpg";
			String photoCmd = "raspistill -o " + pFileName;
			Channel channelPhoto = session.openChannel("exec");
			((ChannelExec) channelPhoto).setCommand(photoCmd);
			channelPhoto.setInputStream(null);
			((ChannelExec) channelPhoto).setErrStream(System.err);

			InputStream inPhoto = channelPhoto.getInputStream();
			channelPhoto.connect();
			byte[] tmp = new byte[1024];
			while (true)
			{
				while (inPhoto.available() > 0)
				{
					int i = inPhoto.read(tmp, 0, 1024);
					if (i < 0)
					{
						break;
					}
					System.out.print(new String(tmp, 0, i));
				}
				if (channelPhoto.isClosed())
				{
					if (inPhoto.available() > 0)
					{
						continue;
					}
					System.out.println("exit-status: " + channelPhoto.getExitStatus());
					break;
				}
				try
				{
					Thread.sleep(1000);
				} catch (Exception ee)
				{
				}
			}
			channelPhoto.disconnect();

			// exec 'scp -f rfile' remotely
			String command = "scp -f " + pFileName;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (true)
			{
				int c = checkAck(in);
				if (c != 'C')
				{
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true)
				{
					if (in.read(buf, 0, 1) < 0)
					{
						// error
						break;
					}
					if (buf[0] == ' ')
					{
						break;
					}
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++)
				{
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a)
					{
						file = new String(buf, 0, i);
						break;
					}
				}

				//System.out.println("filesize="+filesize+", file="+file);
				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				fos = new FileOutputStream(prefix == null ? lfile : prefix + file);
				int foo;
				while (true)
				{
					if (buf.length < filesize)
					{
						foo = buf.length;
					} else
					{
						foo = (int) filesize;
					}
					foo = in.read(buf, 0, foo);
					if (foo < 0)
					{
						// error 
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
					{
						break;
					}
				}
				fos.close();
				fos = null;

				if (checkAck(in) != 0)
				{
					System.exit(0);
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}

			session.disconnect();

			System.exit(0);
		} catch (Exception e)
		{
			System.out.println(e);
			try
			{
				if (fos != null)
				{
					fos.close();
				}
			} catch (Exception ee)
			{
			}
		}
	}

	public static class MyUserInfo
			implements UserInfo, UIKeyboardInteractive
	{

		@Override
		public String getPassword()
		{
			return "mi.45.nt";
		}

		@Override
		public boolean promptYesNo(String str)
		{
			return true;
		}

		@Override
		public String getPassphrase()
		{
			return null;
		}

		@Override
		public boolean promptPassphrase(String message)
		{
			return false;
		}

		@Override
		public boolean promptPassword(String message)
		{
			return true;
		}

		@Override
		public void showMessage(String message)
		{
		}

		@Override
		public String[] promptKeyboardInteractive(String destination,
				String name,
				String instruction,
				String[] prompt,
				boolean[] echo)
		{
			return null;
		}
	}
}
