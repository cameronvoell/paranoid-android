package com.example.cameron.ethereumtest1.database;

public class DBPublication {

    public int publicationID;
    public String name;
    public String metaData;
    public String adminAddress;
  //  public int numAccessListAddresses;
    public int numPublished;
    public int minSupportCostWei;
    public int adminPaymentPercentage;
    public int uniqueSupporters;
    public boolean subscribedLocally;

    public DBPublication (int publicationID, String name, String metaData, String adminAddress,
                          //int numAccessListAddresses,
                          int numPublished, int minSupportCostWei,
                          int adminPaymentPercentage, int uniqueSupporters, boolean subscribedLocally) {
        this.publicationID = publicationID;
        this.name = name;
        this.metaData = metaData;
        this.adminAddress = adminAddress;
       // this.numAccessListAddresses = numAccessListAddresses;
        this.numPublished = numPublished;
        this.minSupportCostWei = minSupportCostWei;
        this.adminPaymentPercentage = adminPaymentPercentage;
        this.uniqueSupporters = uniqueSupporters;
        this.subscribedLocally = subscribedLocally;
    }

}
