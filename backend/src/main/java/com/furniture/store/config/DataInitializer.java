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
             "Trosjed sofa sa dubokim sedištem presvučena sivom tkaninom otpornom na habanje. Visina naslona: 88 cm. Dubina sedišta: 62 cm.",
             "119990", 4, "https://picsum.photos/seed/sofa-skalborg/600/400", dnevna);
        prod("HOLSTEBRO Fotelja",
             "Udobna fotelja s metalnim nogama u srebrnoj boji i tkaninom u boji šampanjca. Nosivost do 120 kg.",
             "39990", 7, "https://picsum.photos/seed/armchair-holstebro/600/400", dnevna);
        prod("BILLUND TV Komoda 150cm",
             "TV komoda sa 2 vrata i otvorenim policama. Dimenzije: 150×40×52 cm. Boja: hrast/crna.",
             "29990", 6, "https://picsum.photos/seed/tv-billund/600/400", dnevna);
        prod("HAMMEL Dnevni Sto Oval",
             "Ovalni dnevni sto sa hrastovim furnirnim vrhom i metalnim nogama. Dim: 110×60×45 cm.",
             "21990", 9, "https://picsum.photos/seed/coffee-hammel/600/400", dnevna);
        prod("SKOVBY Ugaona Sofa L",
             "Modularni ugaoni garnitur L-oblika sa spavaćom funkcijom. Presvlaka: siva tkanina. 270×170 cm.",
             "179990", 2, "https://picsum.photos/seed/corner-sofa/600/400", dnevna);

        // ── Spavaća soba ──────────────────────────────────────────────────────
        prod("DAGESTAD Krevet 160×200",
             "Tapecirani bračni krevet sa uzglavljem presvučenim sivom tkaninom. Bez podnice i madraca.",
             "74990", 5, "https://picsum.photos/seed/bed-dagestad/600/400", spavaca);
        prod("GOLD PREMIUM Madrac 160×200",
             "Ortopedski džepičasti madrac sa 7 zona podrške, visina 24 cm. Presvlaka od 100% pamuka.",
             "59990", 8, "https://picsum.photos/seed/mattress-gold/600/400", spavaca);
        prod("HASLUND Ormar 3-vrata",
             "Garderober sa 3 klizna vrata sa ogledalom, unutrašnje police i šipka. Dim: 150×58×210 cm.",
             "69990", 4, "https://picsum.photos/seed/wardrobe-haslund/600/400", spavaca);
        prod("SOBORG Noćni Stočić",
             "Noćni stočić sa jednom fijasicom i otvorenom policom. Dim: 45×39×58 cm. Boja: bela.",
             "12990", 14, "https://picsum.photos/seed/nightstand-soborg/600/400", spavaca);
        prod("IKAST Komoda 6 Fioka",
             "Komoda sa 6 fioka za organizaciju spavaće sobe. Dim: 80×45×95 cm. Boja: hrast/bela.",
             "34990", 6, "https://picsum.photos/seed/dresser-ikast/600/400", spavaca);

        // ── Trpezarija ────────────────────────────────────────────────────────
        prod("RAMLOSE Trpezarijski Sto Proširivi",
             "Proširivi trpezarijski sto od masivnog hrasta. Bez produžetka: 160×90 cm, sa: 240×90 cm.",
             "79990", 3, "https://picsum.photos/seed/table-ramlose/600/400", trpezarija);
        prod("ADSLEV Trpezarijska Stolica",
             "Set od 2 tapaciranih trpezarijskih stolica sa metalnim nogama u crnoj boji. Tkanina: tamnosiva.",
             "19990", 18, "https://picsum.photos/seed/chair-adslev/600/400", trpezarija);
        prod("EGEDAL Barski Sto",
             "Visoki barski sto sa hrastovim vrhom i crnim metalnim okvirom. Dim: 70×70×105 cm.",
             "27990", 5, "https://picsum.photos/seed/bartable-egedal/600/400", trpezarija);
        prod("BIKER Barska Stolica",
             "Set od 2 barske stolice sa regulacijom visine i metalnom bazom u crnoj boji. Sedište: crna ekokoja.",
             "15990", 10, "https://picsum.photos/seed/barstool-biker/600/400", trpezarija);

        // ── Radna soba ────────────────────────────────────────────────────────
        prod("HALDEN Radni Sto 140cm",
             "Radni sto sa velikom radnom površinom, jednom fijasicom sa brave. Dim: 140×70×75 cm. Bela boja.",
             "29990", 8, "https://picsum.photos/seed/desk-halden/600/400", radna);
        prod("ERGO PRO Kancelarijska Stolica",
             "Ergonomska stolica sa podesivim lumbalnim osloncem, visinom i naslonima za ruke. Mreža leđa.",
             "44990", 6, "https://picsum.photos/seed/office-chair/600/400", radna);
        prod("BILL Polica Za Knjige 5 Nivoa",
             "Modularna polica sa 5 nivo, pogodna za knjige i dekoraciju. Dim: 80×28×170 cm. Crna/hrast.",
             "15990", 12, "https://picsum.photos/seed/bookshelf-bill/600/400", radna);
        prod("STENBY Radni Ormar",
             "Ormar za radnu sobu sa 2 vrata i unutrašnjim policama za arhivu. Dim: 80×40×185 cm.",
             "49990", 4, "https://picsum.photos/seed/office-cabinet/600/400", radna);

        // ── Kupatilo ──────────────────────────────────────────────────────────
        prod("LILLANGEN Ogledalo 60×96cm",
             "Zidno ogledalo sa tankim aluminijumskim okvirom u mat crnoj boji. Dim: 60×96 cm.",
             "8990", 15, "https://picsum.photos/seed/mirror-lillangen/600/400", kupatilo);
        prod("HEMNES Ormarić Za Kupatilo",
             "Viseći ormarić sa ogledalom i unutrašnjim policama. Dim: 63×14×55 cm. Boja: bela.",
             "19990", 8, "https://picsum.photos/seed/cabinet-hemnes/600/400", kupatilo);
        prod("NORRSJON Polica 3 Nivoa",
             "Zidna polica od nerđajućeg čelika sa 3 nivoa za kupatilske potrepštine. Dim: 60×15×70 cm.",
             "5990", 20, "https://picsum.photos/seed/shelf-norrsjon/600/400", kupatilo);
        prod("GODMORGON Komoda Kupatilo",
             "Kupatilska komoda sa 2 fioke i mekom zatvoru. Dim: 80×47×57 cm. Boja: bela sjaj.",
             "32990", 5, "https://picsum.photos/seed/bathroom-dresser/600/400", kupatilo);

        // ── Dečja soba ────────────────────────────────────────────────────────
        prod("FLAXA Krevet Za Dete 90×200",
             "Stabilan dečji krevet sa laticama koje se mogu podesiti na različite visine. Bela boja.",
             "24990", 7, "https://picsum.photos/seed/kid-bed-flaxa/600/400", decja);
        prod("STUVA Radni Sto Dečji",
             "Radni sto za decu sa podesivom visinom od 52-74 cm. Boja: bela. Dim: 90×50 cm.",
             "17990", 9, "https://picsum.photos/seed/kid-desk-stuva/600/400", decja);
        prod("KALLAX Polica Za Igračke",
             "Praktična polica sa 9 polja idealna za kutije i igračke. Dim: 112×42×112 cm. Bela.",
             "14990", 11, "https://picsum.photos/seed/kallax-shelf/600/400", decja);
        prod("FRITIDS Stolica Dečja",
             "Set od 2 dečje stolice sa podesivom visinom 31-45 cm. Boja: plava/bela.",
             "6990", 16, "https://picsum.photos/seed/kid-chair-fritids/600/400", decja);

        // ── Dekoracija ────────────────────────────────────────────────────────
        prod("LINDAU Tepih 160×230cm",
             "Mekani kilerani tepih od 100% polipropilena, lak za čišćenje. Boja: sivo-bežna sa geometrijskim šarama.",
             "22990", 8, "https://picsum.photos/seed/rug-lindau/600/400", dekoracija);
        prod("BROBY Jastuci Set 2kom",
             "Set od 2 dekorativna jastuka 50×50 cm sa uklonjivom navlakom od pamuka. Boja: murski.",
             "3990", 25, "https://picsum.photos/seed/pillows-broby/600/400", dekoracija);
        prod("SKOVBY Podna Lampa",
             "Elegantna podna lampa sa postoljem od mermera i platnom abažurom. Visina: 165 cm.",
             "12990", 6, "https://picsum.photos/seed/floor-lamp/600/400", dekoracija);
        prod("VALLDA Plafonska Lampa",
             "Plafonska svetiljka sa mlečnim staklom i metalnim okvirom. Prečnik: 50 cm. Grlo: E27.",
             "8990", 10, "https://picsum.photos/seed/ceiling-lamp/600/400", dekoracija);

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
