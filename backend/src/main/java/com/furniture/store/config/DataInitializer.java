    package com.furniture.store.config;

import com.furniture.store.model.Category;
import com.furniture.store.model.Product;
import com.furniture.store.model.User;
import com.furniture.store.model.enums.Role;
import com.furniture.store.repository.CategoryRepository;
import com.furniture.store.repository.ProductRepository;
import com.furniture.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUsers();
        createCategoriesAndProducts();
    }

    private void createUsers() {
        if (!userRepository.existsByEmail("admin@furniture.com")) {
            userRepository.save(User.builder()
                    .firstName("Admin").lastName("Store")
                    .email("admin@furniture.com")
                    .password(passwordEncoder.encode("Admin123!"))
                    .role(Role.ADMIN).build());
            log.info("Admin created: admin@furniture.com / Admin123!");
        }
        if (!userRepository.existsByEmail("customer@test.com")) {
            userRepository.save(User.builder()
                    .firstName("Marko").lastName("Marković")
                    .email("customer@test.com")
                    .password(passwordEncoder.encode("Customer123!"))
                    .role(Role.CUSTOMER)
                    .phone("+381641234567")
                    .address("Bulevar Oslobođenja 12, Novi Sad").build());
            log.info("Customer created: customer@test.com / Customer123!");
        }
    }

    private void createCategoriesAndProducts() {
        // Guard: skip if new-format categories already exist
        if (categoryRepository.existsByName("Dnevna soba")) return;

        // ── Categories ────────────────────────────────────────────────────────
        Category dnevna    = cat("Dnevna soba",    "Sofe, fotelje, TV nameštaj i dnevni stolovi");
        Category spavaca   = cat("Spavaća soba",   "Kreveti, madraci, ormari i noćni stolići");
        Category trpezarija = cat("Trpezarija",    "Trpezarijski stolovi, stolice i barski nameštaj");
        Category radna     = cat("Radna soba",      "Radni stolovi, kancelarijske stolice i police");
        Category kupatilo  = cat("Kupatilo",        "Kupatilski nameštaj, police i ogledala");
        Category decja     = cat("Dečja soba",      "Kreveti, stolovi i police za decu");
        Category dekoracija = cat("Dekoracija",     "Tepisi, jastuci, rasveta i ukrasni predmeti");

        // ── Dnevna soba ───────────────────────────────────────────────────────
        prod("SKALBORG Sofa 3-sed",
             "Trosed sofa sa dubokim sedištem presvučena sivom tkaninom otpornom na habanje. Visina naslona: 88 cm. Dubina sedišta: 62 cm.",
             "119990", 4, "https://cdn4.jysk.com/getimage/wd3.large/251793?lock=1", dnevna);
        prod("HOLSTEBRO Fotelja",
             "Udobna fotelja s metalnim nogama u srebrnoj boji i tkaninom u boji šampanjca. Nosivost do 120 kg.",
             "16990", 7, "https://cdn3.jysk.com/getimage/wd3.large/264342?lock=2", dnevna);
        prod("BILLUND TV Komoda 150cm",
             "TV komoda sa 2 vrata i otvorenim policama. Dimenzije: 150×40×52 cm. Boja: hrast/crna.",
             "29990", 6, "https://cdn4.jysk.com/getimage/wd3.large/267154?lock=3", dnevna);
        prod("HAMMEL Dnevni Sto Oval",
             "Ovalni dnevni sto sa hrastovim furnirnim vrhom i metalnim nogama. Dim: 110×60×45 cm.",
             "21990", 9, "https://cdn2.jysk.com/getimage/wd3.large/239060?lock=4", dnevna);
        prod("SKOVBY Ugaona Sofa L",
             "Modularni ugaoni garnitur L-oblika sa spavaćom funkcijom. Presvlaka: siva tkanina. 270×170 cm.",
             "89990", 2, "https://cdn4.jysk.com/getimage/wd3.large/254209?lock=5", dnevna);

        // ── Spavaća soba ──────────────────────────────────────────────────────
        prod("DAGESTAD Krevet 160×200",
             "Tapecirani bračni krevet sa uzglavljem presvučenim sivom tkaninom. Bez podnice i madraca.",
             "74990", 5, "https://cdn1.jysk.com/getimage/wd3.large/263912?lock=6", spavaca);
        prod("GOLD PREMIUM Madrac 160×200",
             "Ortopedski džepičasti madrac sa 7 zona podrške, visina 24 cm. Presvlaka od 100% pamuka.",
             "18990", 8, "https://cdn3.jysk.com/getimage/wd3.large/265187?lock=7", spavaca);
        prod("HASLUND Ormar 3-vrata",
             "Garderober sa 3 klizna vrata sa ogledalom, unutrašnje police i šipka. Dim: 150×58×210 cm.",
             "49990", 4, "https://cdn2.jysk.com/getimage/wd3.medium/273848?lock=8", spavaca);
        prod("SOBORG Noćni Stočić",
             "Noćni stočić sa jednom laticom i otvorenom policom. Dim: 45×39×58 cm. Boja: bela.",
             "6990", 14, "https://cdn2.jysk.com/getimage/wd3.large/263926?lock=9", spavaca);
        prod("IKAST Komoda 6 Fioka",
             "Komoda sa 6 fioka za organizaciju spavaće sobe. Dim: 80×45×95 cm. Boja: hrast/bela.",
             "12990", 6, "https://cdn4.jysk.com/getimage/wd3.large/251361?lock=10", spavaca);

        // ── Trpezarija ────────────────────────────────────────────────────────
        prod("RAMLOSE Trpezarijski Sto Proširivi",
             "Proširivi trpezarijski sto od masivnog hrasta. Bez produžetka: 160×90 cm, sa: 240×90 cm.",
             "35590", 3, "https://cdn4.jysk.com/getimage/wd3.large/259115?lock=11", trpezarija);
        prod("ADSLEV Trpezarijska Stolica",
             "Set od 2 tapaciranih trpezarijskih stolica sa metalnim nogama u crnoj boji. Tkanina: tamnosiva.",
             "6990", 18, "https://cdn1.jysk.com/getimage/wd3.large/257395/?lock=12", trpezarija);
        prod("EGEDAL Barski Sto",
             "Visoki barski sto sa metalnim nogama i podlogom. Dim: 70×70×105 cm.",
             "4990", 5, "https://cdn2.jysk.com/getimage/wd3.large/233186?lock=13", trpezarija);
        prod("BIKER Barska Stolica",
             "Set od 2 barske stolice sa regulacijom visine i metalnom bazom u crnoj boji. Sedište: crna ekokoja.",
             "15990", 10, "https://cdn1.jysk.com/getimage/wd3.large/261350?lock=14", trpezarija);

        // ── Radna soba ────────────────────────────────────────────────────────
        prod("HALDEN Radni Sto 140cm",
             "Radni sto sa velikom radnom površinom. Dim: 140×70×75 cm. Bela boja.",
             "29990", 8, "https://cdn3.jysk.com/getimage/wd3.large/268306?lock=15", radna);
        prod("ERGO PRO Kancelarijska Stolica",
             "Ergonomska stolica sa podesivim lumbalnim osloncem, visinom i naslonima za ruke. Mreža leđa.",
             "12990", 6, "https://cdn1.jysk.com/getimage/wd3.large/253497?lock=16", radna);
        prod("BILL Polica Za Knjige 5 Nivoa",
             "Modularna polica sa 5 nivo, pogodna za knjige i dekoraciju. Dim: 80×28×170 cm. Crna/hrast.",
             "15990", 12, "https://cdn1.jysk.com/getimage/wd3.large/262402?lock=17", radna);
        prod("STENBY Radni Ormar",
             "Ormar za radnu sobu sa 2 vrata i unutrašnjim policama za arhivu. Dim: 80×40×185 cm.",
             "29990", 4, "https://cdn4.jysk.com/getimage/wd3.large/239188?lock=18", radna);

        // ── Kupatilo ──────────────────────────────────────────────────────────
        prod("LILLANGEN Ogledalo 60×96cm",
             "Zidno ogledalo sa tankim aluminijumskim okvirom u mat crnoj boji. Dim: 60×96 cm.",
             "8990", 15, "https://cdn3.jysk.com/getimage/wd3.large/246267?lock=19", kupatilo);
        prod("HEMNES Ormarić Za Kupatilo",
             "Viseći ormarić sa ogledalom i unutrašnjim policama. Dim: 63×14×55 cm. Boja: bela.",
             "19990", 8, "https://www.diplon.net/files/thumbs/files/images/slike_proizvoda/thumbs_800/MRT-06_800_800px.jpg.webp?lock=20", kupatilo);
        prod("NORRSJON Polica 3 Nivoa",
             "Zidna polica od nerđajućeg čelika sa 3 nivoa za kupatilske potrepštine. Dim: 60×15×70 cm.",
             "5990", 20, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQeHiPxXNoOKQl7rhLjDChzT4Zlhq-YfIe5bg&s?lock=21", kupatilo);
        prod("GODMORGON Komoda Kupatilo",
             "Kupatilska komoda sa 2 fioke. Dim: 80×47×57 cm. Boja: bela sjaj.",
             "32990", 5, "https://www.ikea.com/ext/ingkadam/m/df8d90d8fcb27ff/original/PE915386-crop001.JPG?lock=22", kupatilo);

        // ── Dečja soba ────────────────────────────────────────────────────────
        prod("FLAXA Krevet Za Dete 90×200",
             "Stabilan dečji krevet sa laticama koje se mogu podesiti na različite visine. Bela boja.",
             "24990", 7, "https://cdn1.jysk.com/getimage/wd3.large/263925?lock=23", decja);
        prod("STUVA Radni Sto Dečji",
             "Radni sto za decu sa podesivom visinom od 52-74 cm. Boja: bela. Dim: 90×50 cm.",
             "17990", 9, "https://cdn4.jysk.com/getimage/wd3.large/251161?lock=24", decja);
        prod("KALLAX Polica Za Igračke",
             "Praktična polica sa 9 polja idealna za kutije i igračke. Dim: 112×42×112 cm. Bela.",
             "14990", 11, "https://cdn2.jysk.com/getimage/wd3.large/245933?lock=25", decja);
        prod("FRITIDS Stolica Dečja",
             "Set od 2 dečje stolice sa podesivom visinom 31-45 cm. Boja: plava/bela.",
             "6990", 16, "https://cdn2.jysk.com/getimage/wd3.large/267943?lock=26", decja);

        // ── Dekoracija ────────────────────────────────────────────────────────
        prod("LINDAU Tepih 160×230cm",
             "Mekani kilerani tepih od 100% polipropilena, lak za čišćenje. Boja: sivo-bežna sa geometrijskim šarama.",
             "22990", 8, "https://cdn3.jysk.com/getimage/wd3.large/270249?lock=27", dekoracija);
        prod("BROBY Jastuci Set 2kom",
             "Set od 2 dekorativna jastuka 50×50 cm sa uklonjivom navlakom od pamuka. Boja: murski.",
             "3990", 25, "https://cdn4.jysk.com/getimage/wd3.large/237239?lock=28", dekoracija);
        prod("SKOVBY Podna Lampa",
             "Elegantna podna lampa sa postoljem od mermera i platnom abažurom. Visina: 165 cm.",
             "12990", 6, "https://cdn2.jysk.com/getimage/wd3.large/243753?lock=29", dekoracija);
        prod("VALLDA Plafonska Lampa",
             "Plafonska svetiljka sa mlečnim staklom i metalnim okvirom. Prečnik: 50 cm. Grlo: E27.",
             "8990", 10, "https://cdn4.jysk.com/getimage/wd3.large/243769?lock=30", dekoracija);

        log.info("JYSK-inspired categories and {} products created.", productRepository.count());
    }

    private Category cat(String name, String description) {
        return categoryRepository.save(
            Category.builder().name(name).description(description).build()
        );
    }

    private void prod(String name, String description, String price,
                      int stock, String imageUrl, Category category) {
        productRepository.save(Product.builder()
            .name(name).description(description)
            .price(new BigDecimal(price))
            .stockQuantity(stock)
            .imageUrl(imageUrl)
            .category(category)
            .active(true)
            .build());
    }
}
