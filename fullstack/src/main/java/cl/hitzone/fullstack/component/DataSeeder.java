package cl.hitzone.fullstack.component;

import cl.hitzone.fullstack.Model.Role;
import cl.hitzone.fullstack.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Este componente se ejecuta automáticamente cada vez que arranca la aplicación.
 * Se encarga de inicializar datos requeridos por el sistema (como los roles),
 * asegurando que siempre existan en la base de datos.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Inicializar ROLE_USER
        if (roleRepository.findByNameRol("ROLE_USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setNameRol("ROLE_USER");
            userRole.setDescription("Rol por defecto para usuarios básicos");
            roleRepository.save(userRole);
            System.out.println("Seeder: Rol 'ROLE_USER' creado en la base de datos.");
        }

        // 2. Inicializar ROLE_ADMIN
        if (roleRepository.findByNameRol("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setNameRol("ROLE_ADMIN");
            adminRole.setDescription("Rol con privilegios de administrador");
            roleRepository.save(adminRole);
            System.out.println("Seeder: Rol 'ROLE_ADMIN' creado en la base de datos.");
        }
    }
}
