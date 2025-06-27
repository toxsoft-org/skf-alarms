package org.toxsoft.skf.alarms.gui.panels.impl;

/**
 * Alarm sound manager.
 *
 * @author Slavage
 */
public class SoundAlarmManager {

  private SoundAlarmType    type          = SoundAlarmType.NONE;
  private SoundPlayer       currentPlayer = null;
  private final SoundPlayer warningPlayer;
  private final SoundPlayer criticalPlayer;
  private boolean           muted         = false;

  /**
   * sound player context ID
   */
  public static final String CONTEXT_ID = "SoundAlarmManager"; //$NON-NLS-1$

  /**
   * Constructor.
   *
   * @param isWarnNoSound when true no play when warning
   */
  public SoundAlarmManager( boolean isWarnNoSound ) {
    if( isWarnNoSound ) {
      warningPlayer = null;
    }
    else {
      warningPlayer = new SoundPlayer( "sound/warning.wav" ); //$NON-NLS-1$
    }
    // old version
    criticalPlayer = new SoundPlayer( "sound/train.wav" ); //$NON-NLS-1$
    // criticalPlayer = new SoundPlayer( "sound/alarm-critical.wav" ); //$NON-NLS-1$
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
    // Отлючаем старый тип сигнализации.
    stopPlay();
    type = aType;
    if( type != SoundAlarmType.NONE ) {
      // Включаем новый тип сигнализации.
      currentPlayer = (type == SoundAlarmType.WARNING ? warningPlayer : criticalPlayer);
      if( !muted ) {
        startPlay();
      }
    }
  }

  private void startPlay() {
    if( currentPlayer != null ) {
      currentPlayer.start();
    }
  }

  private void stopPlay() {
    if( currentPlayer != null ) {
      currentPlayer.stop();
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
    if( muted ) {
      stopPlay();
    }
    else {
      // restore sound
      if( type != SoundAlarmType.NONE ) {
        // Включаем новый тип сигнализации.
        currentPlayer = (type == SoundAlarmType.WARNING ? warningPlayer : criticalPlayer);
        startPlay();
      }
    }
  }
}
