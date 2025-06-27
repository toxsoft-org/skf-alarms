package org.toxsoft.skf.alarms.gui.panels.impl;

import java.io.*;
import java.net.*;

import javax.sound.sampled.*;

import org.eclipse.core.runtime.*;
import org.osgi.framework.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.alarms.gui.*;

/**
 * Проигрыватель звуковых файлов.
 * <p>
 *
 * @author vs
 */
public class SoundPlayer
    implements ICloseable {

  private File file = null;
  private Clip clip = null;

  boolean stopped = false;

  AudioInputStream inputStream = null;

  /**
   * Конструктор.
   *
   * @param aFilePath String - путь к файлу (ресурсу)
   */
  public SoundPlayer( String aFilePath ) {
    try {
      String pluginId = Activator.PLUGIN_ID;
      Bundle bundle = Platform.getBundle( pluginId );
      URL url = FileLocator.find( bundle, new Path( aFilePath ), null );
      url = FileLocator.toFileURL( url );
      file = URIUtil.toFile( URIUtil.toURI( url ) );
      inputStream = AudioSystem.getAudioInputStream( file );
      init();
    }
    catch( Throwable ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
    finally {
      try {
        if( inputStream != null ) {
          inputStream.close();
        }
      }
      catch( Throwable ex ) {
        LoggerUtils.errorLogger().error( ex );
      }
    }
  }

  /**
   * Начинает проигрывание звукового файла
   */
  public void start() {
    stopped = false;
    if( clip != null ) {
      clip.setFramePosition( 0 );
      clip.start();
    }
  }

  /**
   * Завершает проигрывание звукового файла
   */
  public void stop() {
    stopped = true;
    if( clip != null ) {
      clip.stop();
    }
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса {@link ICloseable}
  //

  @Override
  public void close() {
    stopped = true;
    try {
      inputStream.close();
    }
    catch( Throwable ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
    clip.close();
  }

  // ------------------------------------------------------------------------------------
  // Внутренняя реализация
  //

  private void init() {
    try {
      clip = (Clip)AudioSystem.getLine( new Line.Info( Clip.class ) );
      if( clip == null ) {
        LoggerUtils.errorLogger().error( "Audio clip = null" );
        return;
      }
      clip.addLineListener( event -> {
        if( event.getType() == LineEvent.Type.STOP ) {
          if( !stopped ) {
            try {
              clip.setFramePosition( 0 );
              clip.start();
            }
            catch( Throwable ex ) {
              LoggerUtils.errorLogger().error( ex );
            }
          }
        }
      } );
      clip.open( inputStream );
    }
    catch( Throwable ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
  }

}
