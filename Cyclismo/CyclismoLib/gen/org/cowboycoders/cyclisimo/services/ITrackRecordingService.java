/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/will/code/cyclismo/Cyclismo/CyclismoLib/src/org/cowboycoders/cyclisimo/services/ITrackRecordingService.aidl
 */
package org.cowboycoders.cyclisimo.services;
/**
 * MyTracks service.
 * This service is the process that actually records and manages tracks.
 */
public interface ITrackRecordingService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.cowboycoders.cyclisimo.services.ITrackRecordingService
{
private static final java.lang.String DESCRIPTOR = "org.cowboycoders.cyclisimo.services.ITrackRecordingService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.cowboycoders.cyclisimo.services.ITrackRecordingService interface,
 * generating a proxy if needed.
 */
public static org.cowboycoders.cyclisimo.services.ITrackRecordingService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.cowboycoders.cyclisimo.services.ITrackRecordingService))) {
return ((org.cowboycoders.cyclisimo.services.ITrackRecordingService)iin);
}
return new org.cowboycoders.cyclisimo.services.ITrackRecordingService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_startNewTrack:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.startNewTrack();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_pauseCurrentTrack:
{
data.enforceInterface(DESCRIPTOR);
this.pauseCurrentTrack();
reply.writeNoException();
return true;
}
case TRANSACTION_resumeCurrentTrack:
{
data.enforceInterface(DESCRIPTOR);
this.resumeCurrentTrack();
reply.writeNoException();
return true;
}
case TRANSACTION_endCurrentTrack:
{
data.enforceInterface(DESCRIPTOR);
this.endCurrentTrack();
reply.writeNoException();
return true;
}
case TRANSACTION_isRecording:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isRecording();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isPaused:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isPaused();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getRecordingTrackId:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getRecordingTrackId();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_getTotalTime:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getTotalTime();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_insertWaypoint:
{
data.enforceInterface(DESCRIPTOR);
org.cowboycoders.cyclisimo.content.WaypointCreationRequest _arg0;
if ((0!=data.readInt())) {
_arg0 = org.cowboycoders.cyclisimo.content.WaypointCreationRequest.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
long _result = this.insertWaypoint(_arg0);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_insertTrackPoint:
{
data.enforceInterface(DESCRIPTOR);
android.location.Location _arg0;
if ((0!=data.readInt())) {
_arg0 = android.location.Location.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.insertTrackPoint(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getSensorData:
{
data.enforceInterface(DESCRIPTOR);
byte[] _result = this.getSensorData();
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_getSensorState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSensorState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.cowboycoders.cyclisimo.services.ITrackRecordingService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
   * Starts recording a new track.
   *
   * @return the track ID of the new track.
   */
@Override public long startNewTrack() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startNewTrack, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
    * Pauses the current recording track.
    */
@Override public void pauseCurrentTrack() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pauseCurrentTrack, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
    * Resumes the current recording track.
    */
@Override public void resumeCurrentTrack() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resumeCurrentTrack, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
   * Ends the current recording track.
   */
@Override public void endCurrentTrack() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_endCurrentTrack, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
   * Returns true if currently recording a track.
   */
@Override public boolean isRecording() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isRecording, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
   * Returns true if the current recording track is paused. Returns true if not recording.
   */
@Override public boolean isPaused() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isPaused, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
   * Gets the current recording track ID. Returns -1 if not recording.
   */
@Override public long getRecordingTrackId() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getRecordingTrackId, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
    * Gets the total time for the current recording track. Returns 0 if not recording.
    */
@Override public long getTotalTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTotalTime, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
   * Inserts a waypoint in the current recording track.
   *
   * @param request the details of the waypoint to be inserted
   * @return the ID of the inserted waypoint
   */
@Override public long insertWaypoint(org.cowboycoders.cyclisimo.content.WaypointCreationRequest request) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((request!=null)) {
_data.writeInt(1);
request.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_insertWaypoint, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
   * Inserts a track point in the current recording track.
   *
   * When recording a track, GPS locations are automatically inserted. This is used for
   * inserting special track points or for testing.
   *
   * @param location the track point to be inserted
   */
@Override public void insertTrackPoint(android.location.Location location) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((location!=null)) {
_data.writeInt(1);
location.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_insertTrackPoint, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
   * Gets the current sensor data. Returns null if there is no data.
   
   * @return a byte array of the binary version of the Sensor.SensorDataSet object.
   */
@Override public byte[] getSensorData() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSensorData, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
   * Gets the current sensor manager state.
   * 
   * return a Sensor.SensorState enum value.
   */
@Override public int getSensorState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSensorState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_startNewTrack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_pauseCurrentTrack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_resumeCurrentTrack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_endCurrentTrack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_isRecording = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_isPaused = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getRecordingTrackId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getTotalTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_insertWaypoint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_insertTrackPoint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getSensorData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getSensorState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
}
/**
   * Starts recording a new track.
   *
   * @return the track ID of the new track.
   */
public long startNewTrack() throws android.os.RemoteException;
/**
    * Pauses the current recording track.
    */
public void pauseCurrentTrack() throws android.os.RemoteException;
/**
    * Resumes the current recording track.
    */
public void resumeCurrentTrack() throws android.os.RemoteException;
/**
   * Ends the current recording track.
   */
public void endCurrentTrack() throws android.os.RemoteException;
/**
   * Returns true if currently recording a track.
   */
public boolean isRecording() throws android.os.RemoteException;
/**
   * Returns true if the current recording track is paused. Returns true if not recording.
   */
public boolean isPaused() throws android.os.RemoteException;
/**
   * Gets the current recording track ID. Returns -1 if not recording.
   */
public long getRecordingTrackId() throws android.os.RemoteException;
/**
    * Gets the total time for the current recording track. Returns 0 if not recording.
    */
public long getTotalTime() throws android.os.RemoteException;
/**
   * Inserts a waypoint in the current recording track.
   *
   * @param request the details of the waypoint to be inserted
   * @return the ID of the inserted waypoint
   */
public long insertWaypoint(org.cowboycoders.cyclisimo.content.WaypointCreationRequest request) throws android.os.RemoteException;
/**
   * Inserts a track point in the current recording track.
   *
   * When recording a track, GPS locations are automatically inserted. This is used for
   * inserting special track points or for testing.
   *
   * @param location the track point to be inserted
   */
public void insertTrackPoint(android.location.Location location) throws android.os.RemoteException;
/**
   * Gets the current sensor data. Returns null if there is no data.
   
   * @return a byte array of the binary version of the Sensor.SensorDataSet object.
   */
public byte[] getSensorData() throws android.os.RemoteException;
/**
   * Gets the current sensor manager state.
   * 
   * return a Sensor.SensorState enum value.
   */
public int getSensorState() throws android.os.RemoteException;
}
