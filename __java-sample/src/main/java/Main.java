import com.google.common.base.Throwables;
import json.JSONUtil;
import entity.Company;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author Eric
 * Date 2017/5/9 13:50
 * desc
 */
public class Main {

    public static void main(String[] args) {
        Company company = Company.builder().ifPublic(true).address("address").aDouble(111.111D).build();
        System.out.println(company.toString());
        try {
            System.out.println(JSONUtil.obj2String(company));
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
            e.printStackTrace();
        }

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Company>> set = validator.validate(company);
        for (ConstraintViolation<Company> violation : set) {
            Path path = violation.getPropertyPath();
            System.out.println(path.toString() + ": " + violation.getMessage());
        }
    }

}
