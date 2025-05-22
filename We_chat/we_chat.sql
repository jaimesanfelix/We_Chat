-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 11-04-2025 a las 18:31:59
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `we_chat`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `groups`
--

CREATE TABLE `groups` (
  `id` int(11) NOT NULL,
  `name` varchar(120) NOT NULL,
  `date_creation` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `groups`
--

INSERT INTO `groups` (`id`, `name`, `date_creation`) VALUES
(1, 'Grupo1', '2025-03-11 10:49:06'),
(2, 'Grupo2', '2025-03-11 10:53:48'),
(3, 'Grupo3', '2025-03-11 11:02:42'),
(4, 'Grupo4', '2025-03-11 11:05:24'),
(11, 'Grupo5', '2025-03-13 13:17:17'),
(12, 'Grupo6', '2025-03-13 13:22:05'),
(13, 'GrupoTest', '2025-03-17 13:38:19'),
(14, 'Grupo7', '2025-03-24 12:43:44'),
(15, 'GrupoTest2', '2025-04-01 14:17:42'),
(16, 'GrupoTest3', '2025-04-07 12:26:22'),
(17, 'GrupoTest4', '2025-04-07 12:56:41'),
(18, 'GrupoTest5', '2025-04-08 15:11:30'),
(19, 'GrupoTest6', '2025-04-08 15:15:41'),
(20, 'Grupo5', '2025-04-08 19:17:56');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `user_to_id` int(11) NOT NULL,
  `user_from_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `content` text NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `messages`
--

INSERT INTO `messages` (`id`, `user_to_id`, `user_from_id`, `group_id`, `content`, `timestamp`) VALUES
(1, 8, 7, 0, 'Hola', '2025-03-24 12:56:25'),
(2, 7, 8, 0, 'Adios', '2025-03-24 12:59:18'),
(3, 0, 7, 11, 'Hola Grupo', '2025-03-24 13:08:45'),
(4, 8, 7, 0, 'HOla ', '2025-03-26 16:20:34'),
(5, 8, 7, 0, 'Test', '2025-03-26 16:52:43'),
(6, 0, 7, 0, 'Test Global', '2025-03-26 17:09:16'),
(7, 0, 8, 0, 'Test2', '2025-03-26 17:28:19'),
(8, 8, 7, 0, 'Test Privado', '2025-03-26 17:30:31'),
(9, 8, 7, 0, 'Test privado 2', '2025-03-26 17:31:11'),
(10, 8, 7, 0, 'Holaas', '2025-03-31 10:47:12'),
(11, 7, 8, 0, 'Adioss', '2025-03-31 10:47:45'),
(12, 8, 7, 0, 'Hola tedst', '2025-03-31 10:56:10'),
(13, 8, 7, 0, 'Holasad', '2025-03-31 11:09:04'),
(14, 8, 8, 0, 'Adios testy', '2025-03-31 11:09:27'),
(15, 8, 7, 0, 'Adiostest', '2025-03-31 11:11:28'),
(16, 7, 8, 0, 'Test3124', '2025-03-31 11:13:58'),
(17, 8, 7, 0, 'Hola', '2025-03-31 11:23:45'),
(18, 0, 7, 0, 'asfd', '2025-03-31 11:25:10'),
(19, 8, 7, 0, 'Test2', '2025-03-31 11:34:52'),
(20, 8, 7, 0, 'Test3', '2025-03-31 11:36:28'),
(21, 8, 7, 0, 'Test3', '2025-03-31 11:44:58'),
(22, 0, 7, 0, 'Test4', '2025-03-31 11:45:11'),
(23, 9, 7, 0, 'Test jaime3', '2025-04-01 11:30:38'),
(24, 0, 8, 11, 'Hola grupo2', '2025-04-01 11:58:44'),
(25, 0, 7, 11, 'Hola grupo5', '2025-04-01 12:04:10'),
(26, 0, 7, 11, 'Hola grupo52', '2025-04-01 12:04:42'),
(27, 0, 7, 0, 'Hola', '2025-04-01 12:05:58'),
(28, 0, 7, 0, 'Hola Global2', '2025-04-01 12:16:15'),
(29, 0, 7, 11, 'Hola Grupo5 3', '2025-04-01 12:16:39'),
(30, 0, 7, 15, 'Hola test2', '2025-04-01 12:19:17'),
(31, 0, 9, 0, 'Hola jaime3', '2025-04-01 12:24:45'),
(34, 0, 9, 0, 'Hola', '2025-04-07 10:57:12'),
(36, 0, 9, 17, 'hola', '2025-04-07 10:58:50'),
(37, 0, 7, 17, 'Adios', '2025-04-07 11:02:28'),
(38, 0, 7, 15, 'Hola grupo', '2025-04-07 11:07:48'),
(39, 0, 7, 15, 'Hola grupo2', '2025-04-07 11:09:19'),
(40, 0, 7, 12, 'Hola grupo', '2025-04-07 11:13:20'),
(41, 0, 8, 11, 'Hola grupo21', '2025-04-07 11:13:57'),
(42, 0, 9, 0, 'Hola grupo', '2025-04-08 13:11:42'),
(43, 0, 7, 17, 'Hola hgrupo', '2025-04-08 13:13:47'),
(44, 0, 7, 19, 'Hola grupo6', '2025-04-08 13:15:50'),
(45, 0, 7, 19, 'Hola Grupo62', '2025-04-08 13:16:36'),
(46, 0, 8, 0, 'Hola', '2025-04-08 13:17:32'),
(47, 0, 8, 0, 'Adios', '2025-04-08 13:17:59'),
(48, 8, 7, 0, 'Hola', '2025-04-08 13:25:46'),
(49, 0, 7, 19, 'Hola', '2025-04-08 13:26:30'),
(50, 0, 9, 19, 'Adios', '2025-04-08 13:32:41'),
(51, 0, 9, 19, 'Hola', '2025-04-08 13:37:36'),
(52, 0, 9, 19, 'Adios', '2025-04-08 13:38:51'),
(53, 0, 9, 19, 'Adiosa1', '2025-04-08 13:39:25'),
(54, 0, 9, 19, 'adios2', '2025-04-08 13:39:59'),
(55, 0, 9, 19, 'adios3', '2025-04-08 13:40:25'),
(56, 0, 9, 19, 'adiosd4', '2025-04-08 13:41:06'),
(57, 0, 9, 19, 'adios5', '2025-04-08 13:42:54'),
(58, 0, 9, 19, 'Adios6', '2025-04-08 13:49:29'),
(59, 0, 9, 19, 'Adios7', '2025-04-08 13:50:25'),
(60, 0, 9, 19, 'Adios8', '2025-04-08 13:50:54'),
(61, 0, 9, 19, 'Adios grupo', '2025-04-08 13:51:22'),
(62, 0, 9, 19, 'Adiiosadfa', '2025-04-08 13:51:52'),
(63, 0, 9, 19, 'Hola grupo 6', '2025-04-08 16:32:32'),
(64, 0, 9, 19, 'Hola grupo', '2025-04-08 16:34:45'),
(65, 0, 7, 0, 'Hola', '2025-04-08 16:35:13'),
(66, 7, 8, 0, 'Hola Jaime', '2025-04-08 17:07:49'),
(67, 7, 8, 0, 'Adios Jaime', '2025-04-08 17:08:03'),
(68, 7, 8, 0, 'Hola', '2025-04-08 17:08:54'),
(69, 0, 7, 0, 'Hola', '2025-04-09 14:10:18'),
(70, 0, 7, 0, 'Adios', '2025-04-09 14:10:27'),
(71, 0, 7, 0, 'Hola', '2025-04-09 14:10:48'),
(72, 0, 8, 0, 'Hola', '2025-04-09 14:13:27'),
(73, 8, 7, 0, 'Hola Jaime2', '2025-04-09 14:14:37'),
(74, 0, 9, 19, 'Hola', '2025-04-09 14:16:11'),
(75, 0, 9, 19, 'Hola', '2025-04-09 14:18:13'),
(76, 0, 9, 19, 'Hola', '2025-04-09 14:21:16'),
(77, 0, 9, 19, 'Adf', '2025-04-09 14:21:54'),
(78, 0, 9, 19, 'Hola', '2025-04-09 14:23:53'),
(79, 0, 9, 19, 'Hola', '2025-04-09 14:25:02'),
(80, 0, 9, 19, 'hola', '2025-04-09 14:25:51'),
(81, 9, 9, 0, 'hola', '2025-04-09 14:26:00'),
(82, 0, 7, 0, '#grupo GrupoTest6 hola', '2025-04-09 14:27:42'),
(83, 0, 7, 0, 'holA', '2025-04-09 14:28:14'),
(84, 0, 9, 19, 'hola', '2025-04-09 14:30:06'),
(85, 0, 9, 19, 'HGola', '2025-04-09 14:31:06'),
(86, 0, 9, 19, 'holjd', '2025-04-09 14:33:06'),
(87, 0, 9, 19, 'hola', '2025-04-09 14:33:39'),
(88, 0, 9, 19, 'holasdf', '2025-04-09 14:37:13'),
(89, 0, 9, 19, 'Hsdaf', '2025-04-09 14:37:33'),
(90, 0, 9, 19, 'Hola', '2025-04-09 14:52:43'),
(91, 0, 9, 19, 'hols', '2025-04-09 15:01:16'),
(92, 0, 9, 19, 'adios', '2025-04-09 15:01:30'),
(93, 0, 7, 19, 'holkd', '2025-04-09 15:03:00'),
(94, 0, 9, 19, 'asfq', '2025-04-09 15:06:20'),
(95, 0, 7, 19, 'safqw', '2025-04-09 15:06:52'),
(96, 0, 9, 19, 'qwrq', '2025-04-09 15:07:25'),
(97, 0, 7, 19, 'eqwr', '2025-04-09 15:08:04'),
(98, 0, 9, 19, 'qrq', '2025-04-09 15:09:39'),
(99, 0, 7, 19, 'wqr', '2025-04-09 15:10:12'),
(100, 0, 7, 19, 'qwrqtr', '2025-04-09 15:10:56'),
(101, 0, 7, 19, 'yqeryerw', '2025-04-09 15:11:26'),
(102, 0, 9, 19, 'hola', '2025-04-09 15:33:07'),
(103, 0, 9, 19, 'fq', '2025-04-09 15:34:49'),
(104, 0, 9, 19, 'qrw', '2025-04-09 15:35:37'),
(105, 0, 9, 19, 'qetewqr', '2025-04-09 15:38:57'),
(106, 0, 9, 19, 'yerw2', '2025-04-09 15:42:23'),
(107, 0, 9, 19, 'qr', '2025-04-09 15:45:56'),
(108, 0, 9, 19, 'dh', '2025-04-09 15:47:25'),
(109, 0, 9, 19, 'qwr', '2025-04-09 15:48:41'),
(110, 0, 9, 19, 'dyh', '2025-04-09 15:50:51'),
(111, 0, 9, 19, 'qtew', '2025-04-09 15:51:53'),
(112, 0, 9, 19, 'wrt3', '2025-04-09 15:53:18'),
(113, 0, 9, 19, '13rq', '2025-04-09 15:55:19'),
(114, 0, 9, 19, 'qet2', '2025-04-09 16:00:26'),
(115, 0, 9, 19, 'qwert1', '2025-04-09 16:02:47'),
(116, 0, 9, 19, 'qr1', '2025-04-09 16:05:52'),
(117, 0, 9, 19, '35sa', '2025-04-09 16:07:55'),
(118, 0, 9, 19, 'qwr1', '2025-04-09 16:11:29'),
(119, 15, 16, 0, 'hola', '2025-04-11 16:28:50');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nicknames_by_user`
--

CREATE TABLE `nicknames_by_user` (
  `id` int(11) NOT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_connected_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `nicknames_by_user`
--

INSERT INTO `nicknames_by_user` (`id`, `nickname`, `user_id`, `user_connected_id`) VALUES
(2, 'juanjo1', 14, 15),
(3, 'cate1', 15, 14);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(200) NOT NULL,
  `password` varchar(512) NOT NULL,
  `connection_time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `password`, `connection_time`) VALUES
(7, 'Jaime', 'jaime@gmail.com', 'ehYJ6AkwzFdH0GTZZU4JvwDjUkX/CEmfdLfR8GgRjdImwf3MideDLWaCtZnGy6eL8Rjuy83bx26MCL71Xb88Cf8iY0inTkyayiDNQpIe1LoNGVnAEIkUD0DJBQuWqgFfgyeVe3Mk/5SKEkxXeXglSMngxws4KyCug0tAEoaecQvXWSioNGG8VZnTFLnEQwCF0Bl+H1ZbbdoQEIPV+6+QcVUzH3s+hFy5e4lHZ0aLeH3xk14n1JKTtlqrd9mKPgx+2g4CDyZZ7UUULqoII0hwdK0dksv98SMOnInEZ1mnxj95DNpNh1k+1NWt/A6kR1HRqSrSXnNIwKcbiTe+xDOVDA==', '2025-03-03 10:08:39'),
(8, 'Jaime2', 'jaime2@gmail.com', 'WsAb//lwSw8XYDAsYFJeiEEy2hBQXEOuOd+w82WRZJy2+gkKRFJZM9rOiMd37ZgGqtTaLAZuaLvuENNHq/Vwq++02QyWUFuxVW1BlaCzTAULF/DdPfhuOkPaGz3E9SrGpYqWaVlz3DBZwP10aBA3o49i3rTjbYQMPNXeQTlY0f0rnsvrRxQp6F6QO4NmLmc6vZXw60YCv+OpB6MtpPrjbZolA8Sqe7uW8s4hj39Tma1wGJgYivbK+Ixgcgpjp1gYKMDRqZSuqK/I532Y0xRyRZAOwsVYKHH5RPbnjcMgI12vQTDi4siXrreIjSa1SSuBAMTViUPRRKxXDSk/uzS/3Q==', '2025-03-03 10:09:34'),
(9, 'jaime3', 'jaime3@gmail.com', 'pOmYXNcVUdePDAZDPXuwJvId4yAazHssfrwS01iD/FiePW3ff5TC3NmvaTlliFCeXdT8a+2Xc1khWMKuwWJiGtE1wJ4upTIGgLPlV9h7/aDA7/uPNBAlGqInQf3BVehk+19chKS1Id5lEJPGHccwRvDG5uzlRAdBwUHxhM8BGW5GebPuDihIEwIqf7ESrDSox2TbhklkWxqvrN9qMbvwdpskdDGcvc+uAALRs/v5triu4cAw63wUvwjqHq2kd5i/Zl9RqtiTwg3g2G/qJZEStcdrvQp4D42nyfW+e0vx7HzSbNhL9wHCdtR/aBdeSnUBMuvMngRzAa/NuwfnPGJCUg==', '2025-03-03 11:15:03'),
(10, 'Jaime4', 'jaime4@gmail.com', 'WjpEY31uH4MJIvIYwyX0Ph6WiguybclvoNPOXIFNPLY1feWr9+tsUa/bTO2mpxWZu0vinTsHmfyDtYIdqtg98mdRFO7wC9VO1KuxosbBku3ybK1JEUSw2/7wjeaBqieJJfjEbFXIwAbrMhOD+X9+lpxLmqQ74dHtf0QiRaMWC7Qs7yukzENI6NZFFToeho4PGesrGUIo8f0eEpCLSWM5DCVIplcVD2scLKorT/ixTDXJamrcbfOKOXsyk1oUWar4PUKfTCrAfMnZ6KwC2gBYRLX6N2Ie1CXwjapMxavqqXfMTvcRTF1JXgXRc0j8nEY3LaJa3w1OtGnZHxsEdsnRkA==', '2025-03-24 13:35:35'),
(11, 'Jaime5', 'jaime5@gmail.com', 'Mm0C/8ax84p1TV3egN7djZO2Q0rpC98J80pW4I0hFcQywfmncZZJXh9//YGf8R3UjTz8fhJaMRtBygU6ejK0K5z/sjI4ZtG9k20uHTHEqHhMLYlNBWqPaLHpyZMZV9LsndKsXeQ1ivq2Kd5CFla9Yjhfvb6HmDUtg2j89L5HPf2H267hoguFmdFbTVtoXv9O04itbcmp65BHXkcgPZaYbGr+2F6gAieIvMm7Po9HA++3TpV2zwqiE34V508pPtTrg8EXKDPcARDXNakVktyfMi1pqV5xvU29RQdoSStyE8xsGp2ZlqxV3z5lJkFMQ/Eihpp9ezcxahHo1ORz+3DMdQ==', '2025-03-24 13:35:52'),
(14, 'juanjo', 'juanjo@gmail.com', 'kHUKT+2BH9grjShR05x8voFTaZjR1B+HJM8ZNYR2woQOeZhVUBveOHoVN0dElos2vZNy4SWBaU2xaBH9n+dIIA9M02pZkCzmFuPx8TpjzpGHhAlbE+IzLVYyBnbmJujlAOtwqhLDmKMTpq4cr/t/wxw+uNfddewJIXivgzESQc1Whj5emo/b5VEFi28soCE7X9A9b/GyeD3RdhwqXKTXqxpHuQBpHHq6BDUzQPYh2Zvlo5CkzVa0FV/OKPHhg2MoAVeDzrrzojYbx7MutdfPIMDE/Ccv5nMPZ2znHW1GR/TFFBy8X/Yy2MPUtIN4zS6yNKuaPcY2vr/M2CwJa9beDw==', '2025-04-11 12:32:52'),
(15, 'cate', 'cate@gmail.com', 'r08lvPgQX2mAdlEoPIQUsls97tZdMT+RwfdGwxnP12K3T/1LVfm6wahFW33qXLgq/ll4s4U/CCYsnQUQNN76uRK2QCzvmSPrd2Apqpe5JK5gSqis4EzGSu6bHY9GXoSuR3QZS+J8i8W/YL75CQ9hnnhPUz44cTVvvI1z/vJnOWiRckE/rnNFCXfIsIvHqawwNX6EPM+FwS3MYqC75MgMkBZguFEIzjveDVe8zCs4ymuBzQRmw4b0J8jLYRiXUvamsWTbXhfE6+WjhuMq4golWzYePO+3MeUjNs6opAT/UpQsdVc1BxqPewbxXA8kWY0FbNx1aN+06ZpLReYJJu5WgA==', '2025-04-11 12:39:19'),
(16, 'marina', 'marina@gmail.com', 'EBR4u3eLq02MwA/20DkP8wUyv9wpEbmuSABfrJV3ci2b7slDYNp5H1gZp7RMroKQ9qgH8fmcTD0t5ZyjyswePT9vWCrDeM5tB5981Ji7MK8rfWKbMAhDJElCmXvxy6nvnjyi5/j1MxCvicqyszEM2i77ejEpSbuvjstfH9+kerGQyyJk8Fri3CC8ifMAaHyYm0mkzaiKOakx2j8Nr90I5l8gCElPV9iWGYLtyS0muSjJUG4PQ+VSvT/GYC8yNyPYjuZvY7ya0WS6GfZgxReTmCMuC76ujvpTfmZOrqBEEziCQXkiFXnL4OUazRrANYNzzIZT7KHtRQy1BuxdA5XIfQ==', '2025-04-11 18:28:03');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `users_by_group`
--

CREATE TABLE `users_by_group` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `date_assignation` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `users_by_group`
--

INSERT INTO `users_by_group` (`id`, `user_id`, `group_id`, `date_assignation`) VALUES
(1, 7, 11, '0000-00-00 00:00:00'),
(2, 8, 11, '0000-00-00 00:00:00'),
(3, 7, 12, '2025-03-13 13:22:05'),
(4, 8, 13, '2025-03-17 13:38:19'),
(5, 7, 14, '2025-03-24 12:43:44'),
(6, 8, 14, '2025-03-24 12:43:44'),
(7, 7, 15, '2025-04-01 14:17:42'),
(8, 9, 15, '2025-04-01 14:17:42'),
(9, 7, 16, '2025-04-07 12:26:22'),
(10, 8, 16, '2025-04-07 12:26:22'),
(11, 7, 17, '2025-04-07 12:56:41'),
(12, 9, 17, '2025-04-07 12:56:41'),
(13, 9, 18, '2025-04-08 15:11:30'),
(14, 7, 18, '2025-04-08 15:11:30'),
(15, 7, 19, '2025-04-08 15:15:41'),
(16, 9, 19, '2025-04-08 15:15:41'),
(17, 7, 20, '2025-04-08 19:17:56'),
(18, 8, 20, '2025-04-08 19:17:56');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `nicknames_by_user`
--
ALTER TABLE `nicknames_by_user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nickname_user_id` (`nickname`,`user_id`);

--
-- Indices de la tabla `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nickname` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indices de la tabla `users_by_group`
--
ALTER TABLE `users_by_group`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `groups`
--
ALTER TABLE `groups`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de la tabla `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=120;

--
-- AUTO_INCREMENT de la tabla `nicknames_by_user`
--
ALTER TABLE `nicknames_by_user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `users_by_group`
--
ALTER TABLE `users_by_group`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
