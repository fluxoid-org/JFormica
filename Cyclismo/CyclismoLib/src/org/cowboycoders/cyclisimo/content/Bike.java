package org.cowboycoders.cyclisimo.content;

import android.os.Parcel;
import android.os.Parcelable;

public class Bike implements Parcelable {
  
  public Bike() {};
  
  private String name;
  
  private double weight;
  
  private long id = -1L;
  
  private boolean shared;
  
  private long ownerId;
  
  private Bike(Parcel in) {
    id = in.readLong();
    name = in.readString();
    weight = in.readDouble();
    shared = in.readByte() == 1;
    ownerId = in.readLong();
  }
  
  
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(id);
    dest.writeString(name);
    dest.writeDouble(weight);
    dest.writeByte((byte) (shared == true ? 1 : 0));
    dest.writeLong(ownerId);
  }
  
  public static final Parcelable.Creator<Bike> CREATOR = new Parcelable.Creator<Bike>() {
    @Override
  public Bike createFromParcel(Parcel in) {
    return new Bike(in);
  }

    @Override
  public Bike[] newArray(int size) {
    return new Bike[size];
  }

    
};
  
/**
 * bike name
 * @return
 */
  public String getName() {
    return name;
  }

  /**
   *  bike name
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Weight in kg
   * @return weight
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Weight in kg
   * @param weight
   */
  public void setWeight(double weight) {
    this.weight = weight;
  }


  public long getId() {
    return id;
  }


  public void setId(long id) {
    this.id = id;
  }


  public boolean isShared() {
    return shared;
  }


  public void setShared(boolean shared) {
    this.shared = shared;
  }
  
  public void setShared(int shared) {
    boolean bool = shared == 0 ? false : true;
    setShared(bool);
  }


  public long getOwnerId() {
    return ownerId;
  }


  public void setOwnerId(long ownerId) {
    this.ownerId = ownerId;
  }
  
  


}
