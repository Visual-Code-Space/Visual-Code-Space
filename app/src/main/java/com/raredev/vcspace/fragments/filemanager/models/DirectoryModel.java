package com.raredev.vcspace.fragments.filemanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.File;

public class DirectoryModel implements Parcelable {
  
  private String path;
  private String name;
  
  private DirectoryModel parent;
  private DirectoryModel child;


  public DirectoryModel(String path, String name) {
    this.path = path;
    this.name = name;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public File toFile() {
    return new File(path);
  }

  public static DirectoryModel fileToDirectoryModel(File file) {
    return new DirectoryModel(file.getAbsolutePath(), file.getName());
  }

  public static DirectoryModel fileModelToDirectoryModel(FileModel file) {
    return new DirectoryModel(file.getPath(), file.getName());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeString(path);
    parcel.writeString(name);
  }

  public static final Parcelable.Creator<DirectoryModel> CREATOR =
      new Parcelable.Creator<DirectoryModel>() {
        public DirectoryModel createFromParcel(Parcel in) {
          return new DirectoryModel(in);
        }

        public DirectoryModel[] newArray(int size) {
          return new DirectoryModel[size];
        }
      };

  private DirectoryModel(Parcel parcel) {
    path = parcel.readString();
    name = parcel.readString();
  }
}
