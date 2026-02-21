package com.raver.iemsmaven.Model;

import com.raver.iemsmaven.Controller.ADMIN_EditEmpCTRL.ChildInfo;
import com.raver.iemsmaven.Controller.ADMIN_EditEmpCTRL.ParentInfo;
import com.raver.iemsmaven.Controller.ADMIN_EditEmpCTRL.SpouseInfo;
import com.raver.iemsmaven.Utilities.DatabaseUtil;
// --- CHANGED: Using new utilities ---
import com.raver.iemsmaven.Utilities.Config;
import com.raver.iemsmaven.Utilities.PasswordUtil;
// ---
import com.raver.iemsmaven.Utilities.StringUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// --- CHANGED: Added for UTF-8 conversion ---
import java.nio.charset.StandardCharsets;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author admin
 */
public class User {
    private int id;
    private String fname;
    private String lname;
    private String mname;
    private String suffix;
    private String email;
    private String password;
    private String privilege;
    private String contactNum;
    private String sex;
    private LocalDate birthDate;
    private String residentialAddress;
    private byte[] image;
    private int status;
    private String department;
    private String position;
    private String enrollmentStatus;

    // New CS Form No. 212 fields
    private String placeOfBirth;
    private String civilStatus;
    private Double height;
    private Double weight;
    private String bloodType;
    private String gsisId;
    private String pagibigId;
    private String philhealthNo;
    private String sssNo;
    private String tinNo;
    private String agencyEmployeeNo;
    private String citizenship;
    private String permanentAddress;
    private String telephoneNo;

    // Family Background fields
    private String spouseSurname, spouseFirstName, spouseMiddleName, spouseNameExtension, spouseOccupation, spouseEmployer, spouseBusinessAddress, spouseTelephone;
    private String fatherSurname, fatherFirstName, fatherMiddleName, fatherNameExtension;
    private String motherSurname, motherFirstName, motherMiddleName;
    private String childrenInfo;

    // NEW: Fields for relational data
    private List<EducationalBackground> educationalBackgrounds;
    private List<CivilServiceEligibility> civilServiceEligibilities;
    private List<WorkExperience> workExperiences;

    //Custom variables for Views
    private String fullName;
    private int count;
    private String fullNameWithInitial;
    private String statusString;
    private ObservableList<EmployeeDocument> employeeDocuments;
    
    public ObservableList<EmployeeDocument> getEmployeeDocuments() { return employeeDocuments; }
    public void setEmployeeDocuments(ObservableList<EmployeeDocument> employeeDocuments) { this.employeeDocuments = employeeDocuments; }

    // --- CONSTRUCTORS (ALL PRESERVED + NEW MASTER CONSTRUCTOR) ---
    public User() {
            // Empty constructor for CSV Import
        }
    public User(int userId, String userEmail, String password, String privilege) {
        this.id = userId;
        this.email = userEmail;
        this.password = password;
        this.privilege = privilege;
    }

    //Full Constructor
    public User(
            int id,
            String fname,
            String mname,
            String lname,
            String suffix,
            String email,
            String password,
            String privilege,
            String contactNum,
            String sex,
            LocalDate birthDate,
            String residentialAddress,
            byte[] image,
            int status)
    {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.mname = mname;
        this.suffix = suffix;
        this.email = email;
        this.password = password;
        this.privilege = privilege;
        this.contactNum = contactNum;
        this.sex = sex;
        this.birthDate = birthDate;
        this.residentialAddress = residentialAddress;
        this.image = image;
        this.status = status;
    }

    // Full Constructor with CS Form No. 212 fields (before family)
    public User(
            int id, String fname, String mname, String lname, String suffix, String email, String password,
            String privilege, String contactNum, String sex, LocalDate birthDate, String residentialAddress,
            byte[] image, int status, String placeOfBirth, String civilStatus, Double height, Double weight,
            String bloodType, String gsisId, String pagibigId, String philhealthNo, String sssNo, String tinNo,
            String agencyEmployeeNo, String citizenship, String permanentAddress, String telephoneNo)
    {
        this.id = id; this.fname = fname; this.lname = lname; this.mname = mname; this.suffix = suffix;
        this.email = email; this.password = password; this.privilege = privilege; this.contactNum = contactNum;
        this.sex = sex; this.birthDate = birthDate; this.residentialAddress = residentialAddress; this.image = image;
        this.status = status; this.placeOfBirth = placeOfBirth; this.civilStatus = civilStatus; this.height = height;
        this.weight = weight; this.bloodType = bloodType; this.gsisId = gsisId; this.pagibigId = pagibigId;
        this.philhealthNo = philhealthNo; this.sssNo = sssNo; this.tinNo = tinNo; this.agencyEmployeeNo = agencyEmployeeNo;
        this.citizenship = citizenship; this.permanentAddress = permanentAddress; this.telephoneNo = telephoneNo;
    }

    public User(String email, String password, String privilege){
        this.email = email;
        this.password = password;
        this.privilege = privilege;
    }

    public User(byte[] image, String email, String password, String privilege, String fname, String mname, String lname, String suffix, String sex, LocalDate birthDate, String contactNum, String residentialAddress){
        this.image = image; this.email = email; this.password = password; this.privilege = privilege;
        this.fname = fname; this.mname = mname; this.lname = lname; this.suffix = suffix;
        this.sex = sex; this.birthDate = birthDate; this.contactNum = contactNum; this.residentialAddress = residentialAddress;
    }

    // Constructor for creating new user with ALL PDS fields (used by the form)
    public User(byte[] image, String email, String password, String privilege,
                String fname, String mname, String lname, String suffix,
                String sex, LocalDate birthDate, String contactNum, String residentialAddress,
                String placeOfBirth, String civilStatus, Double height, Double weight,
                String bloodType, String gsisId, String pagibigId, String philhealthNo,
                String sssNo, String tinNo, String agencyEmployeeNo, String citizenship,
                String permanentAddress, String telephoneNo,
                String spouseSurname, String spouseFirstName, String spouseMiddleName,
                String spouseNameExtension, String spouseOccupation, String spouseEmployer,
                String spouseBusinessAddress, String spouseTelephone, String fatherSurname,
                String fatherFirstName, String fatherMiddleName, String fatherNameExtension,
                String motherSurname, String motherFirstName, String motherMiddleName,
                String childrenInfo) {
        this(0, fname, mname, lname, suffix, email, password, privilege, contactNum, sex, birthDate,
            residentialAddress, image, 1, placeOfBirth, civilStatus, height, weight, bloodType,
            gsisId, pagibigId, philhealthNo, sssNo, tinNo, agencyEmployeeNo, citizenship, permanentAddress,
            telephoneNo, spouseSurname, spouseFirstName, spouseMiddleName, spouseNameExtension, spouseOccupation,
            spouseEmployer, spouseBusinessAddress, spouseTelephone, fatherSurname, fatherFirstName,
            fatherMiddleName, fatherNameExtension, motherSurname, motherFirstName, motherMiddleName,
            childrenInfo);
    }
    
    /**
     * NEW MASTER CONSTRUCTOR: Used internally by getUserByUserId to create a User object from the database.
     */
    private User(int id, String fname, String mname, String lname, String suffix, String email, String password,
                 String privilege, String contactNum, String sex, LocalDate birthDate, String residentialAddress,
                 byte[] image, int status, String placeOfBirth, String civilStatus, Double height, Double weight,
                 String bloodType, String gsisId, String pagibigId, String philhealthNo, String sssNo, String tinNo,
                 String agencyEmployeeNo, String citizenship, String permanentAddress, String telephoneNo,
                 String spouseSurname, String spouseFirstName, String spouseMiddleName, String spouseNameExtension,
                 String spouseOccupation, String spouseEmployer, String spouseBusinessAddress, String spouseTelephone,
                 String fatherSurname, String fatherFirstName, String fatherMiddleName, String fatherNameExtension,
                 String motherSurname, String motherFirstName, String motherMiddleName, String childrenInfo) {
        this(id, fname, mname, lname, suffix, email, password, privilege, contactNum, sex, birthDate, residentialAddress, image, status);
        this.placeOfBirth = placeOfBirth; this.civilStatus = civilStatus; this.height = height; this.weight = weight;
        this.bloodType = bloodType; this.gsisId = gsisId; this.pagibigId = pagibigId; this.philhealthNo = philhealthNo;
        this.sssNo = sssNo; this.tinNo = tinNo; this.agencyEmployeeNo = agencyEmployeeNo; this.citizenship = citizenship;
        this.permanentAddress = permanentAddress; this.telephoneNo = telephoneNo; this.spouseSurname = spouseSurname;
        this.spouseFirstName = spouseFirstName; this.spouseMiddleName = spouseMiddleName; this.spouseNameExtension = spouseNameExtension;
        this.spouseOccupation = spouseOccupation; this.spouseEmployer = spouseEmployer; this.spouseBusinessAddress = spouseBusinessAddress;
        this.spouseTelephone = spouseTelephone; this.fatherSurname = fatherSurname; this.fatherFirstName = fatherFirstName;
        this.fatherMiddleName = fatherMiddleName; this.fatherNameExtension = fatherNameExtension; this.motherSurname = motherSurname;
        this.motherFirstName = motherFirstName; this.motherMiddleName = motherMiddleName; this.childrenInfo = childrenInfo;
    }

    // active employee constructor
    public User(int id, String fullName, String privilege, String email, String contactNum, LocalDate birthDate){
        this.id = id; this.fullName = fullName; this.privilege = privilege;
        this.email = email; this.contactNum = contactNum; this.birthDate = birthDate;
    }

    // employee constructor
    public User(int id, String fullName, String privilege, String email, String contactNum, LocalDate birthDate, int status){
        this.id = id; this.fullName = fullName; this.privilege = privilege; this.email = email;
        this.contactNum = contactNum; this.birthDate = birthDate; this.status = status;
    }

    public User(String sex, int count){
        this.sex = sex;
        this.count = count;
    }

    public User(byte[] image){
        this.image = image;
    }

    // --- DATABASE METHODS ---
    
    public static void addUser(User user, List<EducationalBackground> educations, List<CivilServiceEligibility> eligibilities, List<WorkExperience> experiences) throws SQLException {
        Connection conn = null;
        String key = Config.getSecretKey(); // Get the secret key
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // --- CHANGED: SQL Query now uses AES_ENCRYPT ---
            String userQuery = "INSERT INTO `user`(`user_fname`, `user_mname`, `user_lname`, `suffix`, `email`, `password`, `privilege`, `user_cntct`, `sex`, `birth_date`, `residential_address`, `user_img`, `place_of_birth`, `civil_status`, `height`, `weight`, `blood_type`, " + 
                               "`gsis_id`, `pagibig_id`, `philhealth_no`, `sss_no`, `tin_no`, `agency_employee_no`," + // Encrypted fields
                               "`citizenship`, `permanent_address`, `telephone_no`, `spouse_surname`, `spouse_first_name`, `spouse_middle_name`, `spouse_name_extension`, `spouse_occupation`, `spouse_employer`, `spouse_business_address`, `spouse_telephone`, `father_surname`, `father_first_name`, `father_middle_name`, `father_name_extension`, `mother_surname`, `mother_first_name`, `mother_middle_name`, `children_info`) " +
                               "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, " +
                               "AES_ENCRYPT(?, ?), AES_ENCRYPT(?, ?), AES_ENCRYPT(?, ?), AES_ENCRYPT(?, ?), AES_ENCRYPT(?, ?), AES_ENCRYPT(?, ?)," + // Placeholders for encrypted fields
                               "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; // Remaining fields

            try (PreparedStatement psUser = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
                
                psUser.setString(1, emptyToNull(user.getFname()));
                psUser.setString(2, emptyToNull(user.getMname()));
                psUser.setString(3, emptyToNull(user.getLname()));
                psUser.setString(4, emptyToNull(user.getSuffix()));
                psUser.setString(5, emptyToNull(user.getEmail()));
                
                // --- CHANGED: Use PasswordUtil.hashPassword ---
                psUser.setString(6, PasswordUtil.hashPassword(user.getPassword()));
                
                psUser.setString(7, emptyToNull(user.getPrivilege()));
                psUser.setString(8, emptyToNull(user.getContactNum()));
                psUser.setString(9, emptyToNull(user.getSex()));
                psUser.setDate(10, user.getBirthDate() != null ? Date.valueOf(user.getBirthDate()) : null);
                psUser.setString(11, emptyToNull(user.getResidentialAddress()));
                psUser.setBytes(12, user.getImage());
                psUser.setString(13, emptyToNull(user.getPlaceOfBirth()));
                psUser.setString(14, emptyToNull(user.getCivilStatus()));
                
                if (user.getHeight() != null) psUser.setDouble(15, user.getHeight()); 
                else psUser.setNull(15, Types.DOUBLE);
                if (user.getWeight() != null) psUser.setDouble(16, user.getWeight()); 
                else psUser.setNull(16, Types.DOUBLE);
                
                psUser.setString(17, emptyToNull(user.getBloodType()));

                // --- CHANGED: Set encrypted Gov ID fields ---
                psUser.setString(18, emptyToNull(user.getGsisId()));
                psUser.setString(19, key);
                psUser.setString(20, emptyToNull(user.getPagibigId()));
                psUser.setString(21, key);
                psUser.setString(22, emptyToNull(user.getPhilhealthNo()));
                psUser.setString(23, key);
                psUser.setString(24, emptyToNull(user.getSssNo()));
                psUser.setString(25, key);
                psUser.setString(26, emptyToNull(user.getTinNo()));
                psUser.setString(27, key);
                psUser.setString(28, emptyToNull(user.getAgencyEmployeeNo()));
                psUser.setString(29, key);
                // --- End of encrypted fields ---

                psUser.setString(30, emptyToNull(user.getCitizenship()));
                psUser.setString(31, emptyToNull(user.getPermanentAddress()));
                psUser.setString(32, emptyToNull(user.getTelephoneNo()));
                psUser.setString(33, emptyToNull(user.getSpouseSurname()));
                psUser.setString(34, emptyToNull(user.getSpouseFirstName()));
                psUser.setString(35, emptyToNull(user.getSpouseMiddleName()));
                psUser.setString(36, emptyToNull(user.getSpouseNameExtension()));
                psUser.setString(37, emptyToNull(user.getSpouseOccupation()));
                psUser.setString(38, emptyToNull(user.getSpouseEmployer()));
                psUser.setString(39, emptyToNull(user.getSpouseBusinessAddress()));
                psUser.setString(40, emptyToNull(user.getSpouseTelephone()));
                psUser.setString(41, emptyToNull(user.getFatherSurname()));
                psUser.setString(42, emptyToNull(user.getFatherFirstName()));
                psUser.setString(43, emptyToNull(user.getFatherMiddleName()));
                psUser.setString(44, emptyToNull(user.getFatherNameExtension()));
                psUser.setString(45, emptyToNull(user.getMotherSurname()));
                psUser.setString(46, emptyToNull(user.getMotherFirstName()));
                psUser.setString(47, emptyToNull(user.getMotherMiddleName()));
                psUser.setString(48, emptyToNull(user.getChildrenInfo()));

                psUser.executeUpdate();
                
                try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        insertRelatedData(conn, generatedKeys.getInt(1), educations, eligibilities, experiences);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private static String emptyToNull(String value) {
       return (value == null || value.trim().isEmpty() || "Select".equals(value) || "None".equals(value)) ? null : value.trim();
   }

    // --- MISSING METHODS FROM OLD USER.JAVA ---

    public static void addUser(
        String fname,
        String mname,
        String lname,
        String suffix,
        String email,
        String password,
        String privilege,
        String contactNum,
        String sex,
        LocalDate birthDate,
        String residentialAddress,
        byte[] image) throws SQLException
    {
        String hashedPassword = PasswordUtil.hashPassword(password);
        String insertQuery = "INSERT INTO `user`(`user_fname`, `user_mname`, `user_lname`, `suffix`, `email`, `password`, `privilege`, `user_cntct`, `sex`, `birth_date`, `residential_address`, `user_img`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = DatabaseUtil.getConnection().prepareStatement(insertQuery);
            preparedStatement.setString(1, fname.trim());
            preparedStatement.setString(2, mname.trim());
            preparedStatement.setString(3, lname.trim());
            preparedStatement.setString(4, suffix);
            preparedStatement.setString(5, email.trim());
            preparedStatement.setString(6, hashedPassword);
            preparedStatement.setString(7, privilege);
            preparedStatement.setString(8, contactNum.trim());
            preparedStatement.setString(9, sex);
            
            if (birthDate != null) {
                preparedStatement.setDate(10, Date.valueOf(birthDate.toString()));
            } else {
                preparedStatement.setNull(10, java.sql.Types.DATE);
            }

            preparedStatement.setString(11, residentialAddress);
            preparedStatement.setBytes(12, image);
            
            preparedStatement.executeUpdate();
    }
    
    public static void addUser(
        String fname,
        String mname,
        String lname,
        String suffix,
        String email,
        String password,
        String privilege,
        String contactNum,
        String sex,
        LocalDate birthDate,
        String residentialAddress,
        byte[] image,
        // New CS Form No. 212 fields
        String placeOfBirth,
        String civilStatus,
        Double height,
        Double weight,
        String bloodType,
        String gsisId,
        String pagibigId,
        String philhealthNo,
        String sssNo,
        String tinNo,
        String agencyEmployeeNo,
        String citizenship,
        String permanentAddress,
        String telephoneNo) throws SQLException
    {
        String hashedPassword = PasswordUtil.hashPassword(password);
        String insertQuery = "INSERT INTO `user`(`user_fname`, `user_mname`, `user_lname`, `suffix`, `email`, `password`, `privilege`, `user_cntct`, `sex`, `birth_date`, `residential_address`, `user_img`, " +
                           "`place_of_birth`, `civil_status`, `height`, `weight`, `blood_type`, `gsis_id`, `pagibig_id`, `philhealth_no`, `sss_no`, `tin_no`, `agency_employee_no`, `citizenship`, `permanent_address`, `telephone_no`) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = DatabaseUtil.getConnection().prepareStatement(insertQuery);
        preparedStatement.setString(1, fname.trim());
        preparedStatement.setString(2, mname.trim());
        preparedStatement.setString(3, lname.trim());
        preparedStatement.setString(4, suffix);
        preparedStatement.setString(5, email.trim());
        preparedStatement.setString(6, hashedPassword);
        preparedStatement.setString(7, privilege);
        preparedStatement.setString(8, contactNum.trim());
        preparedStatement.setString(9, sex);
        
        if (birthDate != null) {
            preparedStatement.setDate(10, Date.valueOf(birthDate.toString()));
        } else {
            preparedStatement.setNull(10, java.sql.Types.DATE);
        }

        preparedStatement.setString(11, residentialAddress);
        preparedStatement.setBytes(12, image);
        
        // New fields
        preparedStatement.setString(13, placeOfBirth);
        preparedStatement.setString(14, civilStatus);
        if (height != null) {
            preparedStatement.setDouble(15, height);
        } else {
            preparedStatement.setNull(15, java.sql.Types.DOUBLE);
        }
        if (weight != null) {
            preparedStatement.setDouble(16, weight);
        } else {
            preparedStatement.setNull(16, java.sql.Types.DOUBLE);
        }
        preparedStatement.setString(17, bloodType);
        preparedStatement.setString(18, gsisId);
        preparedStatement.setString(19, pagibigId);
        preparedStatement.setString(20, philhealthNo);
        preparedStatement.setString(21, sssNo);
        preparedStatement.setString(22, tinNo);
        preparedStatement.setString(23, agencyEmployeeNo);
        preparedStatement.setString(24, citizenship);
        preparedStatement.setString(25, permanentAddress);
        preparedStatement.setString(26, telephoneNo);
        
        preparedStatement.executeUpdate();
    }

    public static void addUser(
        String fname,
        String mname,
        String lname,
        String suffix,
        String email,
        String password,
        String privilege,
        String contactNum,
        String sex,
        LocalDate birthDate,
        String residentialAddress,
        byte[] image,
        // New CS Form No. 212 fields
        String placeOfBirth,
        String civilStatus,
        Double height,
        Double weight,
        String bloodType,
        String gsisId,
        String pagibigId,
        String philhealthNo,
        String sssNo,
        String tinNo,
        String agencyEmployeeNo,
        String citizenship,
        String permanentAddress,
        String telephoneNo,
        // Family background fields
        String spouseSurname,
        String spouseFirstName,
        String spouseMiddleName,
        String spouseNameExtension,
        String spouseOccupation,
        String spouseEmployer,
        String spouseBusinessAddress,
        String spouseTelephone,
        String fatherSurname,
        String fatherFirstName,
        String fatherMiddleName,
        String fatherNameExtension,
        String motherSurname,
        String motherFirstName,
        String motherMiddleName,
        String childrenInfo) throws SQLException
    {
        String hashedPassword = PasswordUtil.hashPassword(password);
        String insertQuery = "INSERT INTO `user`(`user_fname`, `user_mname`, `user_lname`, `suffix`, `email`, `password`, `privilege`, `user_cntct`, `sex`, `birth_date`, `residential_address`, `user_img`, " +
                           "`place_of_birth`, `civil_status`, `height`, `weight`, `blood_type`, `gsis_id`, `pagibig_id`, `philhealth_no`, `sss_no`, `tin_no`, `agency_employee_no`, `citizenship`, `permanent_address`, `telephone_no`, " +
                           // Family background fields
                           "`spouse_surname`, `spouse_first_name`, `spouse_middle_name`, `spouse_name_extension`, `spouse_occupation`, `spouse_employer`, `spouse_business_address`, `spouse_telephone`, " +
                           "`father_surname`, `father_first_name`, `father_middle_name`, `father_name_extension`, " +
                           "`mother_surname`, `mother_first_name`, `mother_middle_name`, `children_info`) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = DatabaseUtil.getConnection().prepareStatement(insertQuery);
        preparedStatement.setString(1, fname.trim());
        preparedStatement.setString(2, mname.trim());
        preparedStatement.setString(3, lname.trim());
        preparedStatement.setString(4, suffix);
        preparedStatement.setString(5, email.trim());
        preparedStatement.setString(6, hashedPassword);
        preparedStatement.setString(7, privilege);
        preparedStatement.setString(8, contactNum.trim());
        preparedStatement.setString(9, sex);

        if (birthDate != null) {
            preparedStatement.setDate(10, Date.valueOf(birthDate.toString()));
        } else {
            preparedStatement.setNull(10, java.sql.Types.DATE);
        }

        preparedStatement.setString(11, residentialAddress);
        preparedStatement.setBytes(12, image);

        // CS Form No. 212 fields
        preparedStatement.setString(13, placeOfBirth);
        preparedStatement.setString(14, civilStatus);
        if (height != null) {
            preparedStatement.setDouble(15, height);
        } else {
            preparedStatement.setNull(15, java.sql.Types.DOUBLE);
        }
        if (weight != null) {
            preparedStatement.setDouble(16, weight);
        } else {
            preparedStatement.setNull(16, java.sql.Types.DOUBLE);
        }
        preparedStatement.setString(17, bloodType);
        preparedStatement.setString(18, gsisId);
        preparedStatement.setString(19, pagibigId);
        preparedStatement.setString(20, philhealthNo);
        preparedStatement.setString(21, sssNo);
        preparedStatement.setString(22, tinNo);
        preparedStatement.setString(23, agencyEmployeeNo);
        preparedStatement.setString(24, citizenship);
        preparedStatement.setString(25, permanentAddress);
        preparedStatement.setString(26, telephoneNo);

        // Family background fields
        preparedStatement.setString(27, spouseSurname);
        preparedStatement.setString(28, spouseFirstName);
        preparedStatement.setString(29, spouseMiddleName);
        preparedStatement.setString(30, spouseNameExtension);
        preparedStatement.setString(31, spouseOccupation);
        preparedStatement.setString(32, spouseEmployer);
        preparedStatement.setString(33, spouseBusinessAddress);
        preparedStatement.setString(34, spouseTelephone);
        preparedStatement.setString(35, fatherSurname);
        preparedStatement.setString(36, fatherFirstName);
        preparedStatement.setString(37, fatherMiddleName);
        preparedStatement.setString(38, fatherNameExtension);
        preparedStatement.setString(39, motherSurname);
        preparedStatement.setString(40, motherFirstName);
        preparedStatement.setString(41, motherMiddleName);
        preparedStatement.setString(42, childrenInfo);

        preparedStatement.executeUpdate();
    }

    public static void updateUser(
        int userId,
        String fname,
        String mname,
        String lname,
        String suffix,
        String email,
        String password,
        String privilege,
        String contactNum,
        String sex,
        LocalDate birthDate,
        String residentialAddress,
        byte[] image) throws SQLException
    {
        // Hash the new password if it's provided
        String hashedPassword = (password != null) ? PasswordUtil.hashPassword(password) : null;

        String updateQuery = "UPDATE `user` SET `user_fname`=?, `user_mname`=?, `user_lname`=?, `suffix`=?, `email`=?, `password`=?, `privilege`=?, `user_cntct`=?, `sex`=?, `birth_date`=?, `residential_address`=?, `user_img`=? WHERE `user_id`=?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, mname);
            preparedStatement.setString(3, lname);
            preparedStatement.setString(4, suffix);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, hashedPassword);
            preparedStatement.setString(7, privilege);
            preparedStatement.setString(8, contactNum);
            preparedStatement.setString(9, sex);

            if (birthDate != null) {
                preparedStatement.setDate(10, Date.valueOf(birthDate.toString()));
            } else {
                preparedStatement.setNull(10, java.sql.Types.DATE);
            }

            preparedStatement.setString(11, residentialAddress);
            preparedStatement.setBytes(12, image);
            preparedStatement.setInt(13, userId);

            preparedStatement.executeUpdate();
        }
    }
    
    public static void updateUser(
        int userId,
        String fname,
        String mname,
        String lname,
        String suffix,
        String email,
        String password,
        String privilege,
        String contactNum,
        String sex,
        LocalDate birthDate,
        String residentialAddress,
        byte[] image,
        // New CS Form No. 212 fields
        String placeOfBirth,
        String civilStatus,
        Double height,
        Double weight,
        String bloodType,
        String gsisId,
        String pagibigId,
        String philhealthNo,
        String sssNo,
        String tinNo,
        String agencyEmployeeNo,
        String citizenship,
        String permanentAddress,
        String telephoneNo) throws SQLException
    {
        // Hash the new password if it's provided
        String hashedPassword = (password != null) ? PasswordUtil.hashPassword(password) : null;

        String updateQuery = "UPDATE `user` SET `user_fname`=?, `user_mname`=?, `user_lname`=?, `suffix`=?, `email`=?, `password`=?, `privilege`=?, `user_cntct`=?, `sex`=?, `birth_date`=?, `residential_address`=?, `user_img`=?, " +
                           "`place_of_birth`=?, `civil_status`=?, `height`=?, `weight`=?, `blood_type`=?, `gsis_id`=?, `pagibig_id`=?, `philhealth_no`=?, `sss_no`=?, `tin_no`=?, `agency_employee_no`=?, `citizenship`=?, `permanent_address`=?, `telephone_no`=? " +
                           "WHERE `user_id`=?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, mname);
            preparedStatement.setString(3, lname);
            preparedStatement.setString(4, suffix);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, hashedPassword);
            preparedStatement.setString(7, privilege);
            preparedStatement.setString(8, contactNum);
            preparedStatement.setString(9, sex);

            if (birthDate != null) {
                preparedStatement.setDate(10, Date.valueOf(birthDate.toString()));
            } else {
                preparedStatement.setNull(10, java.sql.Types.DATE);
            }

            preparedStatement.setString(11, residentialAddress);
            preparedStatement.setBytes(12, image);
            
            // New fields
            preparedStatement.setString(13, placeOfBirth);
            preparedStatement.setString(14, civilStatus);
            if (height != null) {
                preparedStatement.setDouble(15, height);
            } else {
                preparedStatement.setNull(15, java.sql.Types.DOUBLE);
            }
            if (weight != null) {
                preparedStatement.setDouble(16, weight);
            } else {
                preparedStatement.setNull(16, java.sql.Types.DOUBLE);
            }
            preparedStatement.setString(17, bloodType);
            preparedStatement.setString(18, gsisId);
            preparedStatement.setString(19, pagibigId);
            preparedStatement.setString(20, philhealthNo);
            preparedStatement.setString(21, sssNo);
            preparedStatement.setString(22, tinNo);
            preparedStatement.setString(23, agencyEmployeeNo);
            preparedStatement.setString(24, citizenship);
            preparedStatement.setString(25, permanentAddress);
            preparedStatement.setString(26, telephoneNo);
            preparedStatement.setInt(27, userId);

            preparedStatement.executeUpdate();
        }
    }

    public static void updateUserWithoutPassword(
        int userId,
        String fname,
        String mname,
        String lname,
        String suffix,
        String email,
        String privilege,
        String contactNum,
        String sex,
        LocalDate birthDate,
        String residentialAddress,
        byte[] image) throws SQLException 
    {
        String updateQuery = "UPDATE `user` SET `user_fname`=?, `user_mname`=?, `user_lname`=?, `suffix`=?, `email`=?, `privilege`=?, `user_cntct`=?, `sex`=?, `birth_date`=?, `residential_address`=?, `user_img`=? WHERE `user_id`=?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, mname);
            preparedStatement.setString(3, lname);
            preparedStatement.setString(4, suffix);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, privilege);
            preparedStatement.setString(7, contactNum);
            preparedStatement.setString(8, sex);

            if (birthDate != null) {
                preparedStatement.setDate(9, Date.valueOf(birthDate.toString()));
            } else {
                preparedStatement.setNull(9, java.sql.Types.DATE);
            }

            preparedStatement.setString(10, residentialAddress);
            preparedStatement.setBytes(11, image);
            preparedStatement.setInt(12, userId);

            preparedStatement.executeUpdate();
        }
    }

   public static void updateUserWithoutPassword(
        int userId,
        String fname, String mname, String lname, String suffix, String email, String privilege,
        String contactNum, String sex, LocalDate birthDate, String residentialAddress, byte[] image,
        String placeOfBirth, String civilStatus, Double height, Double weight, String bloodType,
        String gsisId, String pagibigId, String philhealthNo, String sssNo, String tinNo,
        String agencyEmployeeNo, String citizenship, String permanentAddress, String telephoneNo,
        String spouseSurname, String spouseFirstName, String spouseMiddleName, String spouseNameExtension,
        String spouseOccupation, String spouseEmployer, String spouseBusinessAddress, String spouseTelephone,
        String fatherSurname, String fatherFirstName, String fatherMiddleName, String fatherNameExtension,
        String motherSurname, String motherFirstName, String motherMiddleName, String childrenInfo) throws SQLException 
    {
        String key = Config.getSecretKey(); // Get the key
        
        // --- CHANGED: SQL Query now uses AES_ENCRYPT ---
        String updateQuery = "UPDATE `user` SET `user_fname`=?, `user_mname`=?, `user_lname`=?, `suffix`=?, `email`=?, `privilege`=?, `user_cntct`=?, `sex`=?, `birth_date`=?, `residential_address`=?, `user_img`=?, " +
                           "`place_of_birth`=?, `civil_status`=?, `height`=?, `weight`=?, `blood_type`=?, " +
                           "`gsis_id`=AES_ENCRYPT(?, ?), `pagibig_id`=AES_ENCRYPT(?, ?), `philhealth_no`=AES_ENCRYPT(?, ?), `sss_no`=AES_ENCRYPT(?, ?), `tin_no`=AES_ENCRYPT(?, ?), `agency_employee_no`=AES_ENCRYPT(?, ?), " +
                           "`citizenship`=?, `permanent_address`=?, `telephone_no`=?, " +
                           "`spouse_surname`=?, `spouse_first_name`=?, `spouse_middle_name`=?, `spouse_name_extension`=?, `spouse_occupation`=?, `spouse_employer`=?, `spouse_business_address`=?, `spouse_telephone`=?, " +
                           "`father_surname`=?, `father_first_name`=?, `father_middle_name`=?, `father_name_extension`=?, " +
                           "`mother_surname`=?, `mother_first_name`=?, `mother_middle_name`=?, `children_info`=? " +
                           "WHERE `user_id`=?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, mname);
            preparedStatement.setString(3, lname);
            preparedStatement.setString(4, suffix);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, privilege);
            preparedStatement.setString(7, contactNum);
            preparedStatement.setString(8, sex);
            if (birthDate != null) preparedStatement.setDate(9, Date.valueOf(birthDate.toString()));
            else preparedStatement.setNull(9, java.sql.Types.DATE);
            preparedStatement.setString(10, residentialAddress);
            preparedStatement.setBytes(11, image);
            preparedStatement.setString(12, placeOfBirth);
            preparedStatement.setString(13, civilStatus);
            if (height != null) preparedStatement.setDouble(14, height);
            else preparedStatement.setNull(14, java.sql.Types.DOUBLE);
            if (weight != null) preparedStatement.setDouble(15, weight);
            else preparedStatement.setNull(15, java.sql.Types.DOUBLE);
            preparedStatement.setString(16, bloodType);
            
            // --- CHANGED: Set encrypted Gov ID fields ---
            preparedStatement.setString(17, gsisId);
            preparedStatement.setString(18, key);
            preparedStatement.setString(19, pagibigId);
            preparedStatement.setString(20, key);
            preparedStatement.setString(21, philhealthNo);
            preparedStatement.setString(22, key);
            preparedStatement.setString(23, sssNo);
            preparedStatement.setString(24, key);
            preparedStatement.setString(25, tinNo);
            preparedStatement.setString(26, key);
            preparedStatement.setString(27, agencyEmployeeNo);
            preparedStatement.setString(28, key);
            // --- End of encrypted fields ---

            preparedStatement.setString(29, citizenship);
            preparedStatement.setString(30, permanentAddress);
            preparedStatement.setString(31, telephoneNo);
            preparedStatement.setString(32, spouseSurname);
            preparedStatement.setString(33, spouseFirstName);
            preparedStatement.setString(34, spouseMiddleName);
            preparedStatement.setString(35, spouseNameExtension);
            preparedStatement.setString(36, spouseOccupation);
            preparedStatement.setString(37, spouseEmployer);
            preparedStatement.setString(38, spouseBusinessAddress);
            preparedStatement.setString(39, spouseTelephone);
            preparedStatement.setString(40, fatherSurname);
            preparedStatement.setString(41, fatherFirstName);
            preparedStatement.setString(42, fatherMiddleName);
            preparedStatement.setString(43, fatherNameExtension);
            preparedStatement.setString(44, motherSurname);
            preparedStatement.setString(45, motherFirstName);
            preparedStatement.setString(46, motherMiddleName);
            preparedStatement.setString(47, childrenInfo);
            preparedStatement.setInt(48, userId); // The final parameter

            preparedStatement.executeUpdate();
        }
    }

    // --- EXISTING DATABASE METHODS ---

    
   public static User getUserByUserId(int userId) {
        User user = null;
        String key = Config.getSecretKey(); // Get the key
        
        // --- CHANGED: SQL Query now uses AES_DECRYPT ---
        String userQuery = "SELECT user_id, user_fname, user_mname, user_lname, suffix, email, password, privilege, user_cntct, sex, birth_date, residential_address, user_img, user_status, place_of_birth, civil_status, height, weight, blood_type, " +
                           "AES_DECRYPT(gsis_id, ?) AS gsis_id, " +
                           "AES_DECRYPT(pagibig_id, ?) AS pagibig_id, " +
                           "AES_DECRYPT(philhealth_no, ?) AS philhealth_no, " +
                           "AES_DECRYPT(sss_no, ?) AS sss_no, " +
                           "AES_DECRYPT(tin_no, ?) AS tin_no, " +
                           "AES_DECRYPT(agency_employee_no, ?) AS agency_employee_no, " +
                           "citizenship, permanent_address, telephone_no, spouse_surname, spouse_first_name, spouse_middle_name, spouse_name_extension, spouse_occupation, spouse_employer, spouse_business_address, spouse_telephone, father_surname, father_first_name, father_middle_name, father_name_extension, mother_surname, mother_first_name, mother_middle_name, children_info " +
                           "FROM user WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement psUser = conn.prepareStatement(userQuery)) {
            
            int paramIndex = 1;
            // --- CHANGED: Set key for all decrypted fields ---
            psUser.setString(paramIndex++, key);
            psUser.setString(paramIndex++, key);
            psUser.setString(paramIndex++, key);
            psUser.setString(paramIndex++, key);
            psUser.setString(paramIndex++, key);
            psUser.setString(paramIndex++, key);
            psUser.setInt(paramIndex++, userId); // Set the final user_id parameter
            
            ResultSet rs = psUser.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getInt("user_id"), 
                    rs.getString("user_fname"), 
                    rs.getString("user_mname"), 
                    rs.getString("user_lname"), 
                    rs.getString("suffix"), 
                    rs.getString("email"), 
                    rs.getString("password"), // Still need to get password hash for login
                    rs.getString("privilege"), 
                    rs.getString("user_cntct"), 
                    rs.getString("sex"), 
                    rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null, 
                    rs.getString("residential_address"), 
                    rs.getBytes("user_img"), 
                    rs.getInt("user_status"), 
                    rs.getString("place_of_birth"), 
                    rs.getString("civil_status"), 
                    rs.getObject("height") != null ? rs.getDouble("height") : null, 
                    rs.getObject("weight") != null ? rs.getDouble("weight") : null, 
                    rs.getString("blood_type"), 
                    
                    // --- CHANGED: Read decrypted bytes and convert to String ---
                    bytesToString(rs.getBytes("gsis_id")), 
                    bytesToString(rs.getBytes("pagibig_id")), 
                    bytesToString(rs.getBytes("philhealth_no")), 
                    bytesToString(rs.getBytes("sss_no")), 
                    bytesToString(rs.getBytes("tin_no")), 
                    bytesToString(rs.getBytes("agency_employee_no")), 
                    // ---
                    
                    rs.getString("citizenship"), 
                    rs.getString("permanent_address"), 
                    rs.getString("telephone_no"), 
                    rs.getString("spouse_surname"), 
                    rs.getString("spouse_first_name"), 
                    rs.getString("spouse_middle_name"), 
                    rs.getString("spouse_name_extension"), 
                    rs.getString("spouse_occupation"), 
                    rs.getString("spouse_employer"), 
                    rs.getString("spouse_business_address"), 
                    rs.getString("spouse_telephone"), 
                    rs.getString("father_surname"), 
                    rs.getString("father_first_name"), 
                    rs.getString("father_middle_name"), 
                    rs.getString("father_name_extension"), 
                    rs.getString("mother_surname"), 
                    rs.getString("mother_first_name"), 
                    rs.getString("mother_middle_name"), 
                    rs.getString("children_info")
                );
                
                // Load related data (this now uses the same connection, which is good)
                user.setEducationalBackgrounds(getEducationList(conn, userId));
                user.setCivilServiceEligibilities(getEligibilityList(conn, userId));
                user.setWorkExperiences(getWorkExperienceList(conn, userId));
                user.setEmployeeDocuments(getDocumentsByUserId(userId)); // This will now also decrypt
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading user data: " + e.getMessage());
        }
        return user;
    }
    private static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

        private static void insertRelatedData(Connection conn, int userId, List<EducationalBackground> educations, List<CivilServiceEligibility> eligibilities, List<WorkExperience> experiences) throws SQLException {
        // Educational Background
        if (educations != null && !educations.isEmpty()) { 
            String educQuery = "INSERT INTO `educational_background`(`user_id`, `level`, `school_name`, `degree_course`, `start_date`, `end_date`, `units_earned`, `year_graduated`, `honors`) VALUES (?,?,?,?,?,?,?,?,?)"; 
            try (PreparedStatement ps = conn.prepareStatement(educQuery)) { 
                for (EducationalBackground edu : educations) { 
                    ps.setInt(1, userId); 
                    ps.setString(2, edu.getLevel()); 
                    ps.setString(3, edu.getSchoolName()); 
                    ps.setString(4, edu.getDegreeCourse()); 
                    ps.setDate(5, edu.getStartDate() != null ? Date.valueOf(edu.getStartDate()) : null); 
                    ps.setDate(6, edu.getEndDate() != null ? Date.valueOf(edu.getEndDate()) : null); 
                    ps.setString(7, edu.getUnitsEarned()); 
                    ps.setString(8, edu.getYearGraduated()); 
                    ps.setString(9, edu.getHonors()); 
                    ps.addBatch(); 
                } 
                ps.executeBatch(); 
            } 
        }

        // Civil Service Eligibility
        if (eligibilities != null && !eligibilities.isEmpty()) { 
            String eligQuery = "INSERT INTO `civil_service_eligibility`(`user_id`, `eligibility_name`, `rating`, `exam_date`, `exam_place`, `license_number`, `validity_date`) VALUES (?,?,?,?,?,?,?)"; 
            try (PreparedStatement ps = conn.prepareStatement(eligQuery)) { 
                for (CivilServiceEligibility elig : eligibilities) { 
                    ps.setInt(1, userId); 
                    ps.setString(2, elig.getEligibilityName()); 
                    ps.setString(3, elig.getRating()); 
                    ps.setDate(4, elig.getExamDate() != null ? Date.valueOf(elig.getExamDate()) : null); 
                    ps.setString(5, elig.getExamPlace()); 
                    ps.setString(6, elig.getLicenseNumber()); 
                    ps.setDate(7, elig.getValidityDate() != null ? Date.valueOf(elig.getValidityDate()) : null); 
                    ps.addBatch(); 
                } 
                ps.executeBatch(); 
            } 
        }

           if (experiences != null && !experiences.isEmpty()) { 
                String workQuery = "INSERT INTO `work_experience`(`user_id`, `start_date`, `end_date`, `position_title`, `company`, `monthly_salary`, `salary_grade`, `appointment_status`, `is_government_service`) VALUES (?,?,?,?,?,?,?,?,?)"; 
                try (PreparedStatement ps = conn.prepareStatement(workQuery)) { 
                    for (WorkExperience work : experiences) { 
                        System.out.println("DEBUG - Inserting Work Experience:");
                        System.out.println("  Position: " + work.getPositionTitle());
                        System.out.println("  Company: " + work.getCompany());
                        System.out.println("  Monthly Salary: " + work.getMonthlySalary());
                        System.out.println("  Salary Grade: " + work.getSalaryGrade());
                        System.out.println("  Appointment Status: " + work.getAppointmentStatus());
                        System.out.println("  Govt Service: " + work.isGovernmentService());

                        ps.setInt(1, userId); 
                        ps.setDate(2, work.getStartDate() != null ? Date.valueOf(work.getStartDate()) : null); 
                        ps.setDate(3, work.getEndDate() != null ? Date.valueOf(work.getEndDate()) : null); 
                        ps.setString(4, emptyToNull(work.getPositionTitle())); 
                        ps.setString(5, emptyToNull(work.getCompany())); 

                        // FIXED: Make sure monthly salary is set correctly
                        if (work.getMonthlySalary() != null) {
                            ps.setDouble(6, work.getMonthlySalary());
                        } else {
                            ps.setNull(6, Types.DECIMAL);
                        }

                        ps.setString(7, emptyToNull(work.getSalaryGrade())); 
                        ps.setString(8, emptyToNull(work.getAppointmentStatus())); 
                        ps.setBoolean(9, work.isGovernmentService()); 

                        ps.addBatch(); 
                    } 
                    ps.executeBatch(); 
                } 
            }
        }
    private static List<EducationalBackground> getEducationList(Connection conn, int userId) throws SQLException { List<EducationalBackground> list = new ArrayList<>(); String query = "SELECT * FROM educational_background WHERE user_id = ?"; try(PreparedStatement ps = conn.prepareStatement(query)) { ps.setInt(1, userId); ResultSet rs = ps.executeQuery(); while(rs.next()) { list.add(new EducationalBackground(rs.getString("level"), rs.getString("school_name"), rs.getString("degree_course"), rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null, rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null, rs.getString("units_earned"), rs.getString("year_graduated"), rs.getString("honors"))); } } return list; }
    private static List<CivilServiceEligibility> getEligibilityList(Connection conn, int userId) throws SQLException {
        List<CivilServiceEligibility> list = new ArrayList<>();
        String query = "SELECT * FROM civil_service_eligibility WHERE user_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                // Make sure we're properly converting SQL Date to LocalDate
                LocalDate examDate = null;
                LocalDate validityDate = null;

                java.sql.Date sqlExamDate = rs.getDate("exam_date");
                if (sqlExamDate != null) {
                    examDate = sqlExamDate.toLocalDate();
                }

                java.sql.Date sqlValidityDate = rs.getDate("validity_date");
                if (sqlValidityDate != null) {
                    validityDate = sqlValidityDate.toLocalDate();
                }

                list.add(new CivilServiceEligibility(
                    rs.getString("eligibility_name"),
                    rs.getString("rating"),
                    examDate, // This should be LocalDate
                    rs.getString("exam_place"),
                    rs.getString("license_number"),
                    validityDate // This should be LocalDate
                ));
            }
        }
        return list;
    }
    private static List<WorkExperience> getWorkExperienceList(Connection conn, int userId) throws SQLException { 
        List<WorkExperience> list = new ArrayList<>(); 
        String query = "SELECT * FROM work_experience WHERE user_id = ?"; 
        try(PreparedStatement ps = conn.prepareStatement(query)) { 
            ps.setInt(1, userId); 
            ResultSet rs = ps.executeQuery(); 
            while(rs.next()) { 
                // Debug the retrieved data
                System.out.println("DB Work Experience - Position: " + rs.getString("position_title"));
                System.out.println("DB Work Experience - Company: " + rs.getString("company"));
                System.out.println("DB Work Experience - Monthly Salary: " + rs.getDouble("monthly_salary"));
                System.out.println("DB Work Experience - Salary Grade: " + rs.getString("salary_grade"));
                System.out.println("DB Work Experience - Appointment Status: " + rs.getString("appointment_status"));
                System.out.println("DB Work Experience - Gov Service: " + rs.getBoolean("is_government_service"));

                list.add(new WorkExperience(
                    rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null, 
                    rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null, 
                    rs.getString("position_title"), 
                    rs.getString("company"), 
                    rs.getObject("monthly_salary") != null ? rs.getDouble("monthly_salary") : null, 
                    rs.getString("salary_grade"), 
                    rs.getString("appointment_status"), 
                    rs.getBoolean("is_government_service")
                )); 
            } 
        } 
        return list; 
    } // --- ALL OTHER ORIGINAL STATIC METHODS - PRESERVED ---
    
   public static boolean updateUserPassword(int userId, String newPassword) {
        String hashedPassword = PasswordUtil.hashPassword(newPassword); // Use new util
        String sql = "UPDATE user SET password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean isEmailTaken(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean isEmailTaken(String email, int excludeUserId) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ? AND user_id != ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, excludeUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static String getCurrentPassword(int userId) {
        String sql = "SELECT password FROM user WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static int getNextUserId(){
        int nextUserId = 1;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery("SELECT user_id from user order by user_id desc limit 1");
            if (rs.next()) {
                nextUserId = rs.getInt("user_id")+1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextUserId;
    }
    public static ObservableList<User> getUsers(){
        ObservableList<User> users = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery("select * from user where user_status = 1;");
            while (rs.next()) {
                LocalDate birthDate = rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null;
                users.add(new User(rs.getInt("user_id"), rs.getString("user_fname"), rs.getString("user_mname"), rs.getString("user_lname"), rs.getString("suffix"), rs.getString("email"), rs.getString("password"), rs.getString("privilege"), rs.getString("user_cntct"), rs.getString("sex"), birthDate, rs.getString("residential_address"), rs.getBytes("user_img"), rs.getInt("user_status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    public static byte[] getUserImageByUserId(int userId){
        byte[] userImage = null;
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT user_img FROM `user` WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                userImage =rs.getBytes("user_img");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userImage;
    }
    public static User getUserByEmail(String email){
        User user = null;
        try (Connection connection = DatabaseUtil.getConnection()) {
            // Only get what's needed for authentication.
            // The full object will be fetched by getUserByUserId after success.
            String query = "SELECT user_id, email, password, privilege FROM user WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                user = new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("password"), rs.getString("privilege"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    public static ObservableList<User> getActiveEmployees(){
        ObservableList<User> activeEmp = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery("select * from active_employee_view;");
            while (rs.next()) {
                LocalDate birthDate = rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null;
                activeEmp.add(new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("privilege"), rs.getString("email"), rs.getString("user_cntct"), birthDate, 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeEmp;
    }
    public static ObservableList<User> getEmployees(){
        ObservableList<User> activeEmp = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery("select * from employee_view;");
            while (rs.next()) {
                LocalDate birthDate = rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null;
                activeEmp.add(new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("privilege"), rs.getString("email"), rs.getString("user_cntct"), birthDate, rs.getInt("user_status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeEmp;
    }
    public static ObservableList<User> getUserGender(){
        ObservableList<User> user = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT sex, COUNT(DISTINCT(user_id)) as count FROM user WHERE user_status = 1 GROUP BY sex;");
            while (rs.next()) {
                user.add(new User(rs.getString("sex"), rs.getInt("count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    public static void invertUserStatus(int userId) throws SQLException {
        String updateQuery = "UPDATE `user` SET `user_status`= 1 - `user_status` WHERE `user_id`=?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }
    public static boolean isEmailUsed(String email) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";
        try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        }
    }
    public static boolean isEmailUsedExceptForCurrentUser(String email, String currentEmail) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ? AND email != ?";
        try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, currentEmail);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        }
    }
    public static ObservableList<User> getActiveEmployeesWithDepartmentAndPosition() {
        String key = Config.getSecretKey();
        ObservableList<User> users = FXCollections.observableArrayList();
        
        String query = "SELECT u.user_id, u.user_fname, u.user_mname, u.user_lname, u.suffix, " +
                       "u.email, u.password, u.privilege, u.user_cntct, u.sex, u.birth_date, " +
                       "u.residential_address, u.user_img, u.user_status, d.department_name, p.position_name, " +
                       "AES_DECRYPT(u.agency_employee_no, ?) AS agency_employee_no " + 
                       "FROM user u " +
                       "LEFT JOIN assignment a ON u.user_id = a.user_id AND a.status = 1 " +
                       "LEFT JOIN position p ON a.position_id = p.position_id " +
                       "LEFT JOIN department d ON p.department_id = d.department_id " +
                       "WHERE u.user_status = 1 " +
                       "ORDER BY u.user_id ASC";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, key); // <-- NEW: Set the key for decryption
            
            ResultSet rs = preparedStatement.executeQuery();
            // --- END OF MODIFICATIONS ---

            while (rs.next()) {
                LocalDate birthLocalDate = (rs.getDate("birth_date") != null) ?
                        rs.getDate("birth_date").toLocalDate() : null; 
                
                User user = new User(
                    rs.getInt("user_id"), 
                    rs.getString("user_fname"), 
                    rs.getString("user_mname"), 
                    rs.getString("user_lname"), 
                    rs.getString("suffix"), 
                    rs.getString("email"), 
                    rs.getString("password"), 
                    rs.getString("privilege"), 
                    rs.getString("user_cntct"), 
                    rs.getString("sex"), 
                    birthLocalDate, 
                    rs.getString("residential_address"), 
                    rs.getBytes("user_img"), 
                    rs.getInt("user_status")
                ); 
                user.setDepartment(rs.getString("department_name"));
                user.setPosition(rs.getString("position_name")); 
                // --- NEW: Set the decrypted agency employee number ---
                user.setAgencyEmployeeNo(bytesToString(rs.getBytes("agency_employee_no"))); 
                
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users; 
    }
    public static ObservableList<User> getAllEmployeesWithDepartmentAndPosition() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String key = Config.getSecretKey();
        
        String query = "SELECT u.user_id, u.user_fname, u.user_mname, u.user_lname, u.suffix, " +
                       "u.email, u.password, u.privilege, u.user_cntct, u.sex, u.birth_date, " +
                       "u.residential_address, u.user_img, u.user_status, d.department_name, p.position_name, " +
                       "AES_DECRYPT(u.agency_employee_no, ?) AS agency_employee_no " + 
                       "FROM user u " +
                       "LEFT JOIN assignment a ON u.user_id = a.user_id AND a.status = 1 " +
                       "LEFT JOIN position p ON a.position_id = p.position_id " +
                       "LEFT JOIN department d ON p.department_id = d.department_id " +
                       "ORDER BY u.user_id ASC";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, key); // <-- NEW: Set the key for decryption
            
            ResultSet rs = preparedStatement.executeQuery();
            // --- END OF MODIFICATIONS ---

            while (rs.next()) {
                LocalDate birthLocalDate = (rs.getDate("birth_date") != null) ?
                        rs.getDate("birth_date").toLocalDate() : null; 
                
                User user = new User(
                    rs.getInt("user_id"), 
                    rs.getString("user_fname"), 
                    rs.getString("user_mname"), 
                    rs.getString("user_lname"), 
                    rs.getString("suffix"), 
                    rs.getString("email"), 
                    rs.getString("password"), 
                    rs.getString("privilege"), 
                    rs.getString("user_cntct"), 
                    rs.getString("sex"), 
                    birthLocalDate, 
                    rs.getString("residential_address"), 
                    rs.getBytes("user_img"), 
                    rs.getInt("user_status")
                );
                
                user.setDepartment(rs.getString("department_name")); 
                user.setPosition(rs.getString("position_name"));
                
                // --- NEW: Set the decrypted agency employee number ---
                user.setAgencyEmployeeNo(bytesToString(rs.getBytes("agency_employee_no"))); 
                
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // --- ALL GETTERS AND SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }
    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; }
    public String getMname() { return mname; }
    public void setMname(String mname) { this.mname = mname; }
    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPrivilege() { return privilege; }
    public void setPrivilege(String privilege) { this.privilege = privilege; }
    public String getContactNum() { return contactNum; }
    public void setContactNum(String contactNum) { this.contactNum = contactNum; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getResidentialAddress() { return residentialAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }
    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPosition() { return (position == null) ? "" : position; }
    public void setPosition(String position) { this.position = position; }
    public String getPlaceOfBirth() { return placeOfBirth; }
    public void setPlaceOfBirth(String placeOfBirth) { this.placeOfBirth = placeOfBirth; }
    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getGsisId() { return gsisId; }
    public void setGsisId(String gsisId) { this.gsisId = gsisId; }
    public String getPagibigId() { return pagibigId; }
    public void setPagibigId(String pagibigId) { this.pagibigId = pagibigId; }
    public String getPhilhealthNo() { return philhealthNo; }
    public void setPhilhealthNo(String philhealthNo) { this.philhealthNo = philhealthNo; }
    public String getSssNo() { return sssNo; }
    public void setSssNo(String sssNo) { this.sssNo = sssNo; }
    public String getTinNo() { return tinNo; }
    public void setTinNo(String tinNo) { this.tinNo = tinNo; }
    public String getAgencyEmployeeNo() { return agencyEmployeeNo; }
    public void setAgencyEmployeeNo(String agencyEmployeeNo) { this.agencyEmployeeNo = agencyEmployeeNo; }
    public String getCitizenship() { return citizenship; }
    public void setCitizenship(String citizenship) { this.citizenship = citizenship; }
    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }
    public String getTelephoneNo() { return telephoneNo; }
    public void setTelephoneNo(String telephoneNo) { this.telephoneNo = telephoneNo; }
    public String getAddress() { return residentialAddress; }
    public void setAddress(String address) { this.residentialAddress = address; }
    public String getSpouseSurname() { return spouseSurname; }
    public void setSpouseSurname(String spouseSurname) { this.spouseSurname = spouseSurname; }
    public String getSpouseFirstName() { return spouseFirstName; }
    public void setSpouseFirstName(String spouseFirstName) { this.spouseFirstName = spouseFirstName; }
    public String getSpouseMiddleName() { return spouseMiddleName; }
    public void setSpouseMiddleName(String spouseMiddleName) { this.spouseMiddleName = spouseMiddleName; }
    public String getSpouseNameExtension() { return spouseNameExtension; }
    public void setSpouseNameExtension(String spouseNameExtension) { this.spouseNameExtension = spouseNameExtension; }
    public String getSpouseOccupation() { return spouseOccupation; }
    public void setSpouseOccupation(String spouseOccupation) { this.spouseOccupation = spouseOccupation; }
    public String getSpouseEmployer() { return spouseEmployer; }
    public void setSpouseEmployer(String spouseEmployer) { this.spouseEmployer = spouseEmployer; }
    public String getSpouseBusinessAddress() { return spouseBusinessAddress; }
    public void setSpouseBusinessAddress(String spouseBusinessAddress) { this.spouseBusinessAddress = spouseBusinessAddress; }
    public String getSpouseTelephone() { return spouseTelephone; }
    public void setSpouseTelephone(String spouseTelephone) { this.spouseTelephone = spouseTelephone; }
    public String getFatherSurname() { return fatherSurname; }
    public void setFatherSurname(String fatherSurname) { this.fatherSurname = fatherSurname; }
    public String getFatherFirstName() { return fatherFirstName; }
    public void setFatherFirstName(String fatherFirstName) { this.fatherFirstName = fatherFirstName; }
    public String getFatherMiddleName() { return fatherMiddleName; }
    public void setFatherMiddleName(String fatherMiddleName) { this.fatherMiddleName = fatherMiddleName; }
    public String getFatherNameExtension() { return fatherNameExtension; }
    public void setFatherNameExtension(String fatherNameExtension) { this.fatherNameExtension = fatherNameExtension; }
    public String getMotherSurname() { return motherSurname; }
    public void setMotherSurname(String motherSurname) { this.motherSurname = motherSurname; }
    public String getMotherFirstName() { return motherFirstName; }
    public void setMotherFirstName(String motherFirstName) { this.motherFirstName = motherFirstName; }
    public String getMotherMiddleName() { return motherMiddleName; }
    public void setMotherMiddleName(String motherMiddleName) { this.motherMiddleName = motherMiddleName; }
    public String getChildrenInfo() { return childrenInfo; }
    public void setChildrenInfo(String childrenInfo) { this.childrenInfo = childrenInfo; }
    public List<EducationalBackground> getEducationalBackgrounds() { return educationalBackgrounds; }
    public void setEducationalBackgrounds(List<EducationalBackground> educationalBackgrounds) { this.educationalBackgrounds = educationalBackgrounds; }
    public List<CivilServiceEligibility> getCivilServiceEligibilities() { return civilServiceEligibilities; }
    public void setCivilServiceEligibilities(List<CivilServiceEligibility> civilServiceEligibilities) { this.civilServiceEligibilities = civilServiceEligibilities; }
    public List<WorkExperience> getWorkExperiences() { return workExperiences; }
    public void setWorkExperiences(List<WorkExperience> workExperiences) { this.workExperiences = workExperiences; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public String getStatusString() { return statusString; }
    public void setStatusString(String statusString) { this.statusString = statusString; }
    public String getFullNameWithInitial() { 
        System.out.println("Full Name with Initial: " + StringUtil.createFullNameWithInitial(fname, mname, lname, suffix));
        return StringUtil.createFullNameWithInitial(fname, mname, lname, suffix); 
    }
    public void setFullNameWithInitial(String fullNameWithInitial) { this.fullNameWithInitial = fullNameWithInitial; }
    
    public String getFullName() {
        // If fullName field is already set (from employee view), return it
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }

        // Otherwise, build from individual components
        StringBuilder sb = new StringBuilder();

        if (fname != null && !fname.isBlank()) {
            sb.append(fname.trim());
        }
        if (mname != null && !mname.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(mname.trim());
        }
        if (lname != null && !lname.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(lname.trim());
        }
        if (suffix != null && !suffix.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(suffix.trim());
        }

        String full = sb.toString().trim();
        return full.isEmpty() ? "" : full;
    }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public String getStatusDisplay() {
        return status == 1 ? "Active" : "Archived";
    }

    public String getStatusColor() {
        return status == 1 ? "green" : "red";
    }

    public boolean isActive() {
        return status == 1;
    }

    public boolean isArchived() {
        return status == 0;
    }
    
    public static void saveFamilyBackground(int userId, 
                                          List<SpouseInfo> spouses,
                                          List<ParentInfo> parents, 
                                          List<ChildInfo> children) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Clear existing family data
            clearFamilyData(conn, userId);

            // Save spouse
            if (spouses != null && !spouses.isEmpty()) {
                String spouseQuery = "INSERT INTO spouse (user_id, surname, first_name, middle_name, name_extension, occupation, employer, business_address, telephone) VALUES (?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(spouseQuery)) {
                    for (SpouseInfo spouse : spouses) {
                        // Parse full name back to components (you might want to store these separately in SpouseInfo)
                        // For now, we'll store the full name in first_name
                        ps.setInt(1, userId);
                        ps.setString(2, ""); // You'll need to modify SpouseInfo to store components separately
                        ps.setString(3, spouse.getFullName());
                        ps.setString(4, "");
                        ps.setString(5, "");
                        ps.setString(6, spouse.getOccupation());
                        ps.setString(7, spouse.getEmployer());
                        ps.setString(8, spouse.getBusinessAddress());
                        ps.setString(9, spouse.getTelephone());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // Similar implementations for parents and children...

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private static void clearFamilyData(Connection conn, int userId) throws SQLException {
        String[] queries = {
            "DELETE FROM spouse WHERE user_id = ?",
            "DELETE FROM parent WHERE user_id = ?", 
            "DELETE FROM child WHERE user_id = ?"
        };

        for (String query : queries) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
        }
    }
    public static ObservableList<EmployeeDocument> getDocumentsByUserId(int userId) {
        ObservableList<EmployeeDocument> documents = FXCollections.observableArrayList();
        String key = Config.getSecretKey(); // Get the key
        
        // --- CHANGED: SQL Query now uses AES_DECRYPT ---
        String query = "SELECT document_id, user_id, document_type, file_name, " +
                       "AES_DECRYPT(file_data, ?) AS file_data, " +
                       "google_drive_url, uploaded_at " +
                       "FROM employee_documents WHERE user_id = ?";
                       
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, key);  // Set the key
            ps.setInt(2, userId); // Set the user ID
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmployeeDocument doc = new EmployeeDocument(
                    rs.getInt("document_id"),
                    rs.getInt("user_id"),
                    rs.getString("document_type"),
                    rs.getString("file_name"),
                    rs.getBytes("file_data"), // This is now the DECRYPTED data
                    rs.getString("google_drive_url"),
                    rs.getTimestamp("uploaded_at").toLocalDateTime()
                );
                documents.add(doc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    public static void saveOrUpdateDocument(EmployeeDocument document) throws SQLException {
        String key = Config.getSecretKey(); // Get the key
        
        // --- CHANGED: SQL Query now uses AES_ENCRYPT ---
        String query = "INSERT INTO employee_documents (user_id, document_type, file_name, file_data, google_drive_url) " +
                       "VALUES (?, ?, ?, AES_ENCRYPT(?, ?), ?) " + // Encrypt file_data
                       "ON DUPLICATE KEY UPDATE " +
                       "file_name = VALUES(file_name), " +
                       "file_data = AES_ENCRYPT(?, ?), " + // Also encrypt on update
                       "google_drive_url = VALUES(google_drive_url), " +
                       "uploaded_at = CURRENT_TIMESTAMP";
                       
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, document.getUserId());
            ps.setString(2, document.getDocumentType());
            ps.setString(3, document.getFileName());
            
            // --- Set file_data and key for INSERT part ---
            if (document.getFileData() != null) {
                ps.setBytes(4, document.getFileData());
            } else {
                ps.setNull(4, java.sql.Types.BLOB);
            }
            ps.setString(5, key);
            
            ps.setString(6, document.getGoogleDriveUrl());

            // --- Set file_data and key for UPDATE (ON DUPLICATE KEY) part ---
            if (document.getFileData() != null) {
                ps.setBytes(7, document.getFileData());
            } else {
                ps.setNull(7, java.sql.Types.BLOB);
            }
            ps.setString(8, key);
            
            ps.executeUpdate();
        }
    }
    public static ObservableList<String> getUpcomingMilestones() {
        ObservableList<String> milestones = FXCollections.observableArrayList();
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentDay = today.getDayOfMonth();

        String query = "SELECT CONCAT(user_fname, ' ', user_lname) as name, " +
                       "birth_date, " +
                       "(SELECT start_date FROM work_experience WHERE user_id = u.user_id AND is_government_service = 1 ORDER BY start_date ASC LIMIT 1) as service_start " +
                       "FROM user u WHERE user_status = 1";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString("name");
                java.sql.Date birthDateSql = rs.getDate("birth_date");
                java.sql.Date serviceStartSql = rs.getDate("service_start");

                // 1. Check Birthday
                if (birthDateSql != null) {
                    LocalDate birthDate = birthDateSql.toLocalDate();
                    if (birthDate.getMonthValue() == currentMonth) {
                        if (birthDate.getDayOfMonth() == currentDay) {
                            milestones.add("🎂 Today is " + name + "'s Birthday!");
                        } else if (birthDate.getDayOfMonth() > currentDay) {
                            milestones.add("🎂 " + name + "'s Birthday on " + 
                                           birthDate.getMonth().toString().charAt(0) + 
                                           birthDate.getMonth().toString().substring(1).toLowerCase() + 
                                           " " + birthDate.getDayOfMonth());
                        }
                    }
                    
                    // Retirement Watch (60 or 65)
                    int age = Period.between(birthDate, today).getYears();
                    if (age == 59 || age == 64) {
                         // Check if birthday is coming up soon to turn 60/65
                         LocalDate nextBirthday = birthDate.withYear(today.getYear());
                         if (nextBirthday.isAfter(today) && ChronoUnit.DAYS.between(today, nextBirthday) <= 60) {
                             milestones.add("⚠️ Retirement Watch: " + name + " turns " + (age+1) + " soon.");
                         }
                    }
                }

                // 2. Check Service Anniversary
                if (serviceStartSql != null) {
                    LocalDate serviceStart = serviceStartSql.toLocalDate();
                    if (serviceStart.getMonthValue() == currentMonth && serviceStart.getDayOfMonth() >= currentDay) {
                        int years = Period.between(serviceStart, today).getYears();
                        if (years > 0 && years % 5 == 0) { // Every 5 years
                            milestones.add("🏆 " + name + ": " + years + "th Service Anniversary this month.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (milestones.isEmpty()) {
            milestones.add("No upcoming milestones this month.");
        }
        
        return milestones;
    }

    public static void deleteDocument(int documentId) throws SQLException {
        String query = "DELETE FROM employee_documents WHERE document_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, documentId);
            ps.executeUpdate();
        }
    }
    public static boolean hasUser(int userId) {
        String sql = "SELECT 1 FROM user WHERE user_id = ?";
        try (java.sql.Connection conn = com.raver.iemsmaven.Utilities.DatabaseUtil.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Save a user from the Cloud Map
    public static void saveSyncedUser(java.util.Map<String, Object> data) {
        String sql = "INSERT INTO user (user_id, agency_employee_no, user_lname, user_fname, user_mname, " +
                     "sex, email, privilege, user_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = com.raver.iemsmaven.Utilities.DatabaseUtil.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Convert Doubles (from JSON) to Ints carefully
            pstmt.setInt(1, ((Double) data.get("user_id")).intValue()); 

            pstmt.setString(2, (String) data.get("agency_employee_no"));
            pstmt.setString(3, (String) data.get("user_lname"));
            pstmt.setString(4, (String) data.get("user_fname"));
            pstmt.setString(5, (String) data.get("user_mname"));
            pstmt.setString(6, (String) data.get("sex"));
            pstmt.setString(7, (String) data.get("email"));
            pstmt.setString(8, (String) data.get("privilege"));

            // Handle User Status (might be Double or String in JSON)
            int status = 1; 
            if(data.get("user_status") != null) {
                 status = Double.valueOf(data.get("user_status").toString()).intValue();
            }
            pstmt.setInt(9, status);

            pstmt.executeUpdate();
            System.out.println("Synced User Details for ID: " + data.get("user_id"));

        } catch (Exception e) {
            System.err.println("Failed to save synced user: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static int getUserIdByAgencyNo(Connection conn, String agencyNo) throws SQLException {
        String query = "SELECT user_id FROM user WHERE agency_employee_no = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, agencyNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        return -1; // Not found
    }
    
}