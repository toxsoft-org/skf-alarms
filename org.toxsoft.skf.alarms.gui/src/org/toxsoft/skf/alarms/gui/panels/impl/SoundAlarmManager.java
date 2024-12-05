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
      currentPlayer.start();
    }
  }
}
