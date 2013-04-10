package org.cowboycoders.turbotrainers.bushido.brake;

/**
 * States in the calibration sequence. Labels are guessed, so may need changing.
 * @author will
 *
 */
public enum CalibrationState {
    CALIBRATION_MODE,
    CALIBRATION_REQUESTED,
    UP_TO_SPEED, // occurs when slowing down having reached required speed
    SLOWING_DOWN, // after AFTER_UP_TO_SPEED (unknown meaning - possibly could flag an error)
    NO_ERROR,	  // before reached zero speed (another error flag?)
    STOPPED,
    NON_CALIBRATION_MODE, // brake is calibrated ? occurs post-stop aswell as if calibration has not yet been requested
    CALIBRATION_VALUE_READY, // when we can request a calibration value
    CALIBRATED,	  // when we have retrieved calibration value
  }