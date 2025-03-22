package org.toxsoft.skf.alarms.gui.panels.impl;

/**
 * Alarm sound manager.
 *
 * @author Slavage
 */
public class SoundAlarmManager {

  private SoundAlarmType type           = SoundAlarmType.NONE;
  private SoundPlayer    currentPlayer  = null;
  private SoundPlayer    warningPlayer  = null;
  private SoundPlayer    criticalPlayer = null;
  private boolean        muted          = false;

  /**
   * sound player context ID
   */
  public static final String CONTEXT_ID = "SoundAlarmManager"; //$NON-NLS-1$

  /**
   * Constructor.
   */
  public SoundAlarmManager() {
    warningPlayer = new SoundPlayer( "sound/warning.wav" ); //$NON-NLS-1$
    criticalPlayer = new SoundPlayer( "sound/train.wav" ); //$NON-NLS-1$
  }

  /**
   * Returns the alarm type.
   *
   * @return the alarm type.
   */
  public SoundAlarmType getType() {
    return type;
  }

  /**
   * Change alarm type.
   * <p>
   * Updating the status of the sound alarm, i.s. turning on, turning off.
   *
   * @param aType the alarm type.
   */
  public void setType( SoundAlarmType aType ) {
    if( type == aType ) {
      // nothing to do
      return;
    }
    if( currentPlayer != null ) {
      // Отлючаем старый тип сигнализации.
      currentPlayer.stop();
    }
    type = aType;
    if( type != SoundAlarmType.NONE ) {
      // Включаем новый тип сигнализации.
      currentPlayer = (type == SoundAlarmType.WARNING ? warningPlayer : criticalPlayer);
      if( !muted ) {
        currentPlayer.start();
      }
    }
  }

  /**
   * Mute/unmute players
   * <p>
   *
   * @param aMuteFlag - true - turn all sounds off.
   */
  public void setMuted( boolean aMuteFlag ) {
    muted = aMuteFlag;
    if( muted && currentPlayer != null ) {
      currentPlayer.stop();
    }
    else {
      // restore sound
      if( type != SoundAlarmType.NONE ) {
        // Включаем новый тип сигнализации.
        currentPlayer = (type == SoundAlarmType.WARNING ? warningPlayer : criticalPlayer);
        currentPlayer.start();
      }
    }
  }
}
